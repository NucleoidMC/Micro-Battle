package io.github.haykam821.microbattle.game.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.kit.Kit;
import io.github.haykam821.microbattle.game.map.MicroBattleMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class MicroBattleActivePhase {
	private final ServerWorld world;
	private final GameSpace gameSpace;
	private final MicroBattleMap map;
	private final MicroBattleConfig config;
	private final Set<PlayerEntry> players;
	private boolean singleplayer;
	private boolean opened;

	public MicroBattleActivePhase(GameSpace gameSpace, MicroBattleMap map, MicroBattleConfig config, Set<PlayerEntry> players) {
		this.world = gameSpace.getWorld();
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.players = players;
	}

	public static void open(GameSpace gameSpace, MicroBattleMap map, MicroBattleConfig config) {
		Set<PlayerEntry> players = gameSpace.getPlayers().stream().map(PlayerEntry::new).collect(Collectors.toSet());
		MicroBattleActivePhase phase = new MicroBattleActivePhase(gameSpace, map, config, players);

		gameSpace.openGame(game -> {
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.BREAK_BLOCKS, RuleResult.ALLOW);
			game.setRule(GameRule.CRAFTING, RuleResult.DENY);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.ALLOW);
			game.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
			game.setRule(GameRule.PLACE_BLOCKS, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.ALLOW);
			game.setRule(GameRule.THROW_ITEMS, RuleResult.ALLOW);

			// Listeners
			game.on(GameOpenListener.EVENT, phase::open);
			game.on(GameTickListener.EVENT, phase::tick);
			game.on(PlayerAddListener.EVENT, phase::addPlayer);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
			game.on(PlayerRemoveListener.EVENT, phase::onPlayerRemove);
		});
	}

	private void open() {
		this.opened = true;
		this.singleplayer = this.players.size() == 1;

		List<Kit> kits = new ArrayList<>(this.config.getKits());
		if (kits.size() == 0) throw new GameOpenException(new TranslatableText("text.microbattle.not_enough_kits"));
		Collections.shuffle(kits);

		int index = 0;
 		for (PlayerEntry entry : this.players) {
			entry.getPlayer().setGameMode(GameMode.SURVIVAL);
			MicroBattleActivePhase.spawn(this.world, this.map, entry.getPlayer());

			Kit kit = kits.get(index % kits.size());
			entry.setKit(kit);
			entry.applyInventory();

			index += 1;
		}
	}

	private boolean isInVoid(ServerPlayerEntity player) {
		return player.getY() < this.map.getFullBounds().getMin().getY();
	}

	private void tick() {
		// Eliminate players that are out of bounds or in the void
		Iterator<PlayerEntry> playerIterator = this.players.iterator();
		while (playerIterator.hasNext()) {
			PlayerEntry entry = playerIterator.next();
			entry.tick();

			ServerPlayerEntity player = entry.getPlayer();
			if (!this.map.getFullBounds().contains(player.getBlockPos())) {
				this.eliminate(entry, this.isInVoid(player) ? ".void" : ".out_of_bounds", false);
				playerIterator.remove();
			}
		}

		// Attempt to determine a winner
		if (this.players.size() < 2) {
			if (this.players.size() == 1 && this.singleplayer) return;

			this.gameSpace.getPlayers().sendMessage(this.getEndingMessage());
			this.gameSpace.close(GameCloseReason.FINISHED);
		}
	}

	private Text getEndingMessage() {
		if (this.players.size() == 1) {
			PlayerEntity winner = this.players.iterator().next().getPlayer();
			return new TranslatableText("text.microbattle.win", winner.getDisplayName()).formatted(Formatting.GOLD);
		}
		return new TranslatableText("text.microbattle.no_winners").formatted(Formatting.GOLD);
	}

	private void setSpectator(ServerPlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
	}

	private void addPlayer(ServerPlayerEntity player) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry == null || !this.players.contains(entry)) {
			this.setSpectator(player);
		} else if (this.opened) {
			this.eliminate(entry, true);
		}
	}

	private void eliminate(PlayerEntry entry, Text message, boolean remove) {
		this.gameSpace.getPlayers().sendMessage(message);

		if (remove) {
			this.players.remove(entry);
		}
		this.setSpectator(entry.getPlayer());
	}

	private void eliminate(PlayerEntry entry, String suffix, boolean remove) {
		this.eliminate(entry, new TranslatableText("text.microbattle.eliminated" + suffix, entry.getPlayer().getDisplayName()).formatted(Formatting.RED), remove);
	}

	private void eliminate(PlayerEntry entry, boolean remove) {
		this.eliminate(entry, "", remove);
	}

	private PlayerEntry getEntryFromPlayer(ServerPlayerEntity player) {
		for (PlayerEntry entry : this.players) {
			if (player.equals(entry.getPlayer())) {
				return entry;
			}
		}
		return null;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			if (source.getAttacker() == null) {
				this.eliminate(entry, ".killed", true);
			} else {
				this.eliminate(entry, new TranslatableText("text.microbattle.eliminated.killed.by", player.getDisplayName(), source.getAttacker().getDisplayName()).formatted(Formatting.RED), true);
			}
		} else {
			MicroBattleActivePhase.spawn(this.world, this.map, player);
		}
		return ActionResult.FAIL;
	}
	
	public void onPlayerRemove(ServerPlayerEntity player) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			this.eliminate(entry, true);
		}
	}

	public static void spawn(ServerWorld world, MicroBattleMap map, ServerPlayerEntity player) {
		Vec3d center = map.getInnerBounds().getCenter();
		player.teleport(world, center.getX(), map.getInnerBounds().getMin().getY() + 1, center.getZ(), 0, 0);
	}
}