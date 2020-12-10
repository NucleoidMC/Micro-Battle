package io.github.haykam821.microbattle.game.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.map.MicroBattleMap;
import io.github.haykam821.microbattle.game.win.FreeForAllWinManager;
import io.github.haykam821.microbattle.game.win.TeamWinManager;
import io.github.haykam821.microbattle.game.win.WinManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.event.GameCloseListener;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.event.UseBlockListener;
import xyz.nucleoid.plasmid.game.player.GameTeam;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class MicroBattleActivePhase {
	private final ServerWorld world;
	private final GameSpace gameSpace;
	private final MicroBattleMap map;
	private final MicroBattleConfig config;
	private final List<Team> teams;
	private final Set<PlayerEntry> players;
	private final WinManager winManager;
	private boolean singleplayer;
	private boolean opened;

	public MicroBattleActivePhase(GameSpace gameSpace, MicroBattleMap map, TeamSelectionLobby teamSelection, MicroBattleConfig config) {
		this.world = gameSpace.getWorld();
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;

		Map<ServerPlayerEntity, GameTeam> playersToGameTeams = new HashMap<>();
		this.teams = new ArrayList<>();

		MinecraftServer server = gameSpace.getWorld().getServer();
		ServerScoreboard scoreboard = server.getScoreboard();

		if (teamSelection != null) {
			teamSelection.allocate((gameTeam, player) -> {
				playersToGameTeams.put(player, gameTeam);

				Team team = createTeam(gameTeam, server);
				teams.add(team);
				scoreboard.addPlayerToTeam(player.getEntityName(), team);
			});
		}

		this.players = gameSpace.getPlayers().stream().map(entity -> {
			return new PlayerEntry(this, entity, playersToGameTeams.get(entity));
		}).collect(Collectors.toSet());;
		this.winManager = teamSelection == null ? new FreeForAllWinManager(this) : new TeamWinManager(this);
	}

	public static void open(GameSpace gameSpace, MicroBattleMap map, TeamSelectionLobby teamSelection, MicroBattleConfig config) {
		MicroBattleActivePhase phase = new MicroBattleActivePhase(gameSpace, map, teamSelection, config);

		gameSpace.openGame(game -> {
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.BREAK_BLOCKS, RuleResult.ALLOW);
			game.setRule(GameRule.CRAFTING, RuleResult.DENY);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(Main.FLUID_FLOW, RuleResult.DENY);
			game.setRule(GameRule.HUNGER, RuleResult.ALLOW);
			game.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
			game.setRule(GameRule.PLACE_BLOCKS, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.ALLOW);
			game.setRule(GameRule.TEAM_CHAT, RuleResult.ALLOW);
			game.setRule(GameRule.THROW_ITEMS, RuleResult.ALLOW);

			// Listeners
			game.on(GameCloseListener.EVENT, phase::onClose);
			game.on(GameOpenListener.EVENT, phase::open);
			game.on(GameTickListener.EVENT, phase::tick);
			game.on(PlayerAddListener.EVENT, phase::addPlayer);
			game.on(PlayerDamageListener.EVENT, phase::onPlayerDamage);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
			game.on(PlayerRemoveListener.EVENT, phase::onPlayerRemove);
			game.on(UseBlockListener.EVENT, phase::onUseBlock);
		});
	}

	private void open() {
		this.opened = true;
		this.singleplayer = this.players.size() == 1;

		List<KitType<?>> kitTypes = new ArrayList<>(this.config.getKits());
		if (kitTypes.size() == 0) throw new GameOpenException(new TranslatableText("text.microbattle.not_enough_kits"));
		Collections.shuffle(kitTypes);

		int index = 0;
 		for (PlayerEntry entry : this.players) {
			entry.getPlayer().setGameMode(GameMode.SURVIVAL);

			KitType<?> kitType = kitTypes.get(index % kitTypes.size());
			entry.initializeKit(kitType.create(entry));

			index += 1;
		}
	}

	private boolean isInVoid(ServerPlayerEntity player) {
		return player.getY() < this.map.getFullBounds().getMin().getY();
	}

	private Text getOutOfBoundsMessage(ServerPlayerEntity player) {
		String type = this.isInVoid(player) ? "void" : "out_of_bounds";
		if (player.getPrimeAdversary() == null) {
			return new TranslatableText("text.microbattle.eliminated." + type, player.getDisplayName()).formatted(Formatting.RED);
		} else {
			return new TranslatableText("text.microbattle.eliminated." + type + ".by", player.getDisplayName(), player.getPrimeAdversary().getDisplayName()).formatted(Formatting.RED);
		}
	}

	private void tick() {
		// Eliminate players that are out of bounds or in the void
		Iterator<PlayerEntry> playerIterator = this.players.iterator();
		while (playerIterator.hasNext()) {
			PlayerEntry entry = playerIterator.next();
			entry.tick();

			ServerPlayerEntity player = entry.getPlayer();
			if (!this.map.getFullBounds().contains(player.getBlockPos())) {
				this.eliminate(entry, this.getOutOfBoundsMessage(player), false);
				playerIterator.remove();
			}
		}

		// Attempt to determine a winner
		if (this.winManager.checkForWinner()) {
			gameSpace.close(GameCloseReason.FINISHED);
		}
	}

	public GameSpace getGameSpace() {
		return this.gameSpace;
	}

	public Set<PlayerEntry> getPlayers() {
		return this.players;
	}

	public boolean isSingleplayer() {
		return this.singleplayer;
	}

	private void onClose() {
		ServerScoreboard scoreboard = this.gameSpace.getWorld().getServer().getScoreboard();
		for (Team team : this.teams) {
			scoreboard.removeTeam(team);
		}
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

	private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			return entry.getKit().onUseBlock(hand, hitResult);
		}

		return ActionResult.PASS;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry == null) {
			MicroBattleActivePhase.spawn(this.world, this.map, player);
		} else {
			this.eliminate(entry, source.getDeathMessage(player).shallowCopy().formatted(Formatting.RED), true);
		}
		return ActionResult.FAIL;
	}

	private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		PlayerEntry target = this.getEntryFromPlayer(player);
		if (target == null) return ActionResult.PASS;
		if (!(source.getAttacker() instanceof ServerPlayerEntity)) return ActionResult.PASS;

		PlayerEntry attacker = this.getEntryFromPlayer((ServerPlayerEntity) source.getAttacker());
		if (attacker != null && target.isSameTeam(attacker)) {
			return ActionResult.FAIL;
		}

		ActionResult damagedResult = attacker.getKit().onDamaged(target, source, amount);
		if (damagedResult != ActionResult.PASS) return damagedResult;

		return attacker.getKit().onDealDamage(target, source, amount);
	}
	
	public void onPlayerRemove(ServerPlayerEntity player) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			this.eliminate(entry, true);
		}
	}

	public boolean isOldCombat() {
		return this.config.isOldCombat();
	}

	private static Team createTeam(GameTeam gameTeam, MinecraftServer server) {
		ServerScoreboard scoreboard = server.getScoreboard();
		String key = RandomStringUtils.randomAlphanumeric(16);

		Team team = scoreboard.getTeam(key);
		if (team == null) {
			team = scoreboard.addTeam(key);
		}

		// Display
		team.setDisplayName(new LiteralText(gameTeam.getDisplay()));
		team.setColor(gameTeam.getFormatting());

		// Rules
		team.setFriendlyFireAllowed(false);
		team.setShowFriendlyInvisibles(true);
		team.setCollisionRule(Team.CollisionRule.PUSH_OTHER_TEAMS);

		return team;
	}

	public static void spawn(ServerWorld world, MicroBattleMap map, ServerPlayerEntity player) {
		Vec3d center = map.getFloorBounds().getCenter();
		int xOffset = (map.getRiverRadius() + 2) * (world.getRandom().nextBoolean() ? 1 : -1);
		int zOffset = (map.getRiverRadius() + 2) * (world.getRandom().nextBoolean() ? 1 : -1);

		player.teleport(world, center.getX() + xOffset + 0.5, map.getFloorBounds().getMax().getY(), center.getZ() + zOffset + 0.5, 0, 0);
	}
}