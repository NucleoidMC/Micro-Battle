package io.github.haykam821.microbattle.game.phase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.event.AfterBlockPlaceListener;
import io.github.haykam821.microbattle.game.event.PlayDeathSoundListener;
import io.github.haykam821.microbattle.game.event.PlayHurtSoundListener;
import io.github.haykam821.microbattle.game.kit.Kit;
import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.kit.RespawnerKit;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectionManager;
import io.github.haykam821.microbattle.game.map.MicroBattleMap;
import io.github.haykam821.microbattle.game.win.FreeForAllWinManager;
import io.github.haykam821.microbattle.game.win.TeamWinManager;
import io.github.haykam821.microbattle.game.win.WinManager;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.api.game.common.team.TeamChat;
import xyz.nucleoid.plasmid.api.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.api.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class MicroBattleActivePhase {
	private final ServerLevel world;
	private final GameSpace gameSpace;
	private final MicroBattleMap map;
	private final MicroBattleConfig config;
	private final Set<PlayerEntry> players;
	private final Set<PlayerEntry> eliminatedPlayers = new HashSet<>();
	private final TeamManager teamManager;
	private final WinManager winManager;
	private boolean singleplayer;
	private int ticksUntilClose = -1;

	public MicroBattleActivePhase(GameSpace gameSpace, ServerLevel world, MicroBattleMap map, TeamManager teamManager, KitSelectionManager kitSelection, MicroBattleConfig config) {
		this.world = world;
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;

		PlayerSet participants = this.gameSpace.getPlayers().participants();
		this.players = new HashSet<>(participants.size());

		this.teamManager = teamManager;
		this.winManager = teamManager == null ? new FreeForAllWinManager(this) : new TeamWinManager(this);

		for (ServerPlayer player : participants) {
			GameTeamKey team = teamManager == null ? null : teamManager.teamFor(player);
			KitType<?> kitType = kitSelection.get(player, this.world.getRandom());

			this.players.add(new PlayerEntry(this, player, team, kitType));
		}
	}

	public static void open(GameSpace gameSpace, ServerLevel world, MicroBattleMap map, TeamSelectionLobby teamSelection, KitSelectionManager kitSelection, MicroBattleConfig config) {
		gameSpace.setActivity(activity -> {
			MicroBattleActivePhase phase;
			if (teamSelection == null) {
				phase = new MicroBattleActivePhase(gameSpace, world, map, null, kitSelection, config);
			} else {
				TeamManager teamManager = TeamManager.addTo(activity);
				TeamChat.addTo(activity, teamManager);
				
				for (GameTeam team : config.getTeams().get()) {
					teamManager.addTeam(team);
				}

				teamSelection.allocate(gameSpace.getPlayers().participants(), (team, player) -> {
					teamManager.addPlayerTo(player, team);
				});

				phase = new MicroBattleActivePhase(gameSpace, world, map, teamManager, kitSelection, config);
			}

			activity.allow(GameRuleType.BLOCK_DROPS);
			activity.allow(GameRuleType.BREAK_BLOCKS);
			activity.deny(GameRuleType.CRAFTING);
			activity.allow(GameRuleType.FALL_DAMAGE);
			activity.deny(GameRuleType.FLUID_FLOW);
			activity.allow(GameRuleType.HUNGER);
			activity.allow(GameRuleType.INTERACTION);
			activity.deny(GameRuleType.MODIFY_ARMOR);
			activity.allow(GameRuleType.PLACE_BLOCKS);
			activity.allow(GameRuleType.PLAYER_PROJECTILE_KNOCKBACK);
			activity.deny(GameRuleType.PORTALS);
			activity.allow(GameRuleType.PVP);
			activity.allow(GameRuleType.THROW_ITEMS);

			// Listeners
			activity.listen(AfterBlockPlaceListener.EVENT, phase::afterBlockPlace);
			activity.listen(BlockBreakEvent.EVENT, phase::onBreakBlock);
			activity.listen(GameActivityEvents.ENABLE, phase::enable);
			activity.listen(GameActivityEvents.TICK, phase::tick);
			activity.listen(PlayDeathSoundListener.EVENT, phase::playDeathSound);
			activity.listen(PlayHurtSoundListener.EVENT, phase::playHurtSound);
			activity.listen(GamePlayerEvents.ACCEPT, phase::onAcceptPlayers);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::acceptSpectators);
			activity.listen(PlayerDamageEvent.EVENT, phase::onPlayerDamage);
			activity.listen(PlayerDeathEvent.EVENT, phase::onPlayerDeath);
			activity.listen(GamePlayerEvents.REMOVE, phase::onPlayerRemove);
			activity.listen(BlockUseEvent.EVENT, phase::onUseBlock);
			activity.listen(ItemThrowEvent.EVENT, phase::onThrowItem);
		});
	}

	private void enable() {
		this.singleplayer = this.players.size() == 1;

 		for (PlayerEntry entry : this.players) {
			entry.getPlayer().setGameMode(GameType.SURVIVAL);
			entry.getPlayer().closeContainer();

			entry.initializeKit();
		}

		for (ServerPlayer player : this.gameSpace.getPlayers().spectators()) {
			MicroBattleActivePhase.spawn(this.world, this.map, player);
			this.setSpectator(player);
		}
	}

	private boolean isInVoid(ServerPlayer player) {
		return player.getY() < this.map.getFullBounds().min().getY();
	}

	private Component getCustomEliminatedMessage(ServerPlayer player, String type) {
		if (player.getKillCredit() == null) {
			return Component.translatable("text.microbattle.eliminated." + type, player.getDisplayName()).withStyle(ChatFormatting.RED);
		} else {
			return Component.translatable("text.microbattle.eliminated." + type + ".by", player.getDisplayName(), player.getKillCredit().getDisplayName()).withStyle(ChatFormatting.RED);
		}
	}

	private void tick() {
		// Decrease ticks until game end to zero
		if (this.isGameEnding()) {
			if (this.ticksUntilClose == 0) {
				this.gameSpace.close(GameCloseReason.FINISHED);
			}

			this.ticksUntilClose -= 1;
			return;
		}

		// Eliminate players that are out of bounds or in the void
		Iterator<PlayerEntry> playerIterator = this.players.iterator();
		while (playerIterator.hasNext()) {
			PlayerEntry entry = playerIterator.next();
			entry.tick();

			ServerPlayer player = entry.getPlayer();
			if (!this.map.getFullBounds().contains(player.blockPosition())) {
				if (this.isInVoid(player)) {
					if (this.applyToKit(entry, kit -> kit.attemptRespawn()) == EventResult.ALLOW) {
						break;
					}
					this.eliminate(entry, this.getCustomEliminatedMessage(player, "void"));
				} else {
					entry.tickOutOfBounds();
				}
			}
		}

		for (PlayerEntry entry : this.eliminatedPlayers) {
			this.players.remove(entry);
		}
		this.eliminatedPlayers.clear();

		// Attempt to determine a winner
		if (this.winManager.checkForWinner()) {
			this.endGame();
		}
	}

	private SoundEvent playDeathSound(LivingEntity entity, SoundEvent defaultSound) {
		PlayerEntry entry = this.getEntryFromEntity(entity);
		if (entry == null) return null;

		return entry.getKit().getDeathSound();
	}

	private SoundEvent playHurtSound(LivingEntity entity, DamageSource source, SoundEvent defaultSound) {
		PlayerEntry entry = this.getEntryFromEntity(entity);
		if (entry == null) return null;

		return entry.getKit().getHurtSound(source);
	}

	public GameSpace getGameSpace() {
		return this.gameSpace;
	}

	public ServerLevel getWorld() {
		return this.world;
	}

	public Set<PlayerEntry> getPlayers() {
		return this.players;
	}

	public GameTeamConfig getTeamConfig(GameTeamKey teamKey) {
		return this.teamManager.getTeamConfig(teamKey);
	}

	public boolean isSingleplayer() {
		return this.singleplayer;
	}

	private void endGame() {
		this.ticksUntilClose = this.config.getTicksUntilClose().sample(this.world.getRandom());
	}

	private boolean isGameEnding() {
		return this.ticksUntilClose >= 0;
	}

	private void setSpectator(ServerPlayer player) {
		player.setGameMode(GameType.SPECTATOR);
	}

	private JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return acceptor.teleport(this.world, MicroBattleActivePhase.getSpawnPos(this.world, this.map)).thenRunForEach(player -> {
			this.setSpectator(player);
		});
	}

	private void eliminate(PlayerEntry entry, Component message) {
		if (!this.isGameEnding()) {
			this.gameSpace.getPlayers().sendMessage(message);
			this.eliminatedPlayers.add(entry);
			entry.onEliminated();
		}
	}

	private void eliminate(PlayerEntry entry, String suffix) {
		this.eliminate(entry, Component.translatable("text.microbattle.eliminated" + suffix, entry.getPlayer().getDisplayName()).withStyle(ChatFormatting.RED));
	}

	private void eliminate(PlayerEntry entry) {
		this.eliminate(entry, "");
	}

	private PlayerEntry getEntryFromPlayer(ServerPlayer player) {
		for (PlayerEntry entry : this.players) {
			if (player.equals(entry.getPlayer())) {
				return entry;
			}
		}
		return null;
	}

	private PlayerEntry getEntryFromEntity(Entity entity) {
		if (entity instanceof ServerPlayer player) {
			return this.getEntryFromPlayer(player);
		}

		return null;
	}

	private InteractionResult onUseBlock(ServerPlayer player, InteractionHand hand, BlockHitResult hitResult) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			return entry.getKit().onUseBlock(hand, hitResult);
		}

		return InteractionResult.PASS;
	}

	private EventResult onPlayerDeath(ServerPlayer player, DamageSource source) {
		PlayerEntry entry = this.getEntryFromPlayer(player);

		EventResult kitResult = this.applyToKit(entry, kit -> kit.onDeath(source));
		if (kitResult != EventResult.PASS) return kitResult;

		PlayerEntry killer = this.getEntryFromEntity(source.getEntity());
		EventResult killerKitResult = this.applyToKit(killer, kit -> kit.onKilledPlayer(entry, source));
		if (killerKitResult != EventResult.PASS) return killerKitResult;

		if (entry == null) {
			MicroBattleActivePhase.spawn(this.world, this.map, player);
		} else if (!this.map.getFullBounds().contains(player.blockPosition())) {
			this.eliminate(entry, this.getCustomEliminatedMessage(player, "out_of_bounds"));
		} else if (this.applyToKit(entry, kit -> kit.attemptRespawn()) != EventResult.ALLOW) {
			this.eliminate(entry, source.getLocalizedDeathMessage(player).copy().withStyle(ChatFormatting.RED));
		}
		
		return EventResult.DENY;
	}

	public boolean placeBeacon(PlayerEntry entry, RespawnerKit respawner, BlockPos pos) {
		if (respawner.hasRespawnPos()) return true;
		if (!this.map.getBeaconBounds().contains(pos)) {
			entry.getPlayer().sendSystemMessage(Component.translatable("text.microbattle.cannot_place_out_of_bounds_beacon").withStyle(ChatFormatting.RED), false);
			return false;
		}
		respawner.setRespawnPos(pos);
		return true;
	}

	/**
	 * Applies a function on a player's kit if they have one.
	 */
	private EventResult applyToKit(PlayerEntry entry, Function<Kit, EventResult> function) {
		if (entry == null || entry.getKit() == null) return EventResult.PASS;
		return function.apply(entry.getKit());
	}

	private EventResult afterBlockPlace(BlockPos pos, Level world, ServerPlayer player, ItemStack stack, BlockState state) {
		PlayerEntry placer = this.getEntryFromPlayer(player);
		if (placer == null) return EventResult.PASS;

		return this.applyToKit(placer, kit -> kit.afterBlockPlace(pos, stack, state));
	}

	private EventResult onBreakBlock(ServerPlayer player, ServerLevel world, BlockPos pos) {
		PlayerEntry breaker = this.getEntryFromPlayer(player);
		if (breaker == null) return EventResult.PASS;

		EventResult kitResult = this.applyToKit(breaker, kit -> kit.onBreakBlock(pos));
		if (kitResult != EventResult.PASS) return kitResult;

		// Prevent breaking non-beacons
		BlockState state = player.level().getBlockState(pos);
		if (!state.is(Main.RESPAWN_BEACONS)) return EventResult.ALLOW;

		// Send message
		for (PlayerEntry entry : this.players) {
			if (entry.getKit().isRespawnPos(pos, true)) {
				this.gameSpace.getPlayers().playSound(SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1, 1);
				this.gameSpace.getPlayers().sendMessage(Component.translatable("text.microbattle.beacon_break", entry.getPlayer().getDisplayName(), breaker.getPlayer().getDisplayName()).withStyle(ChatFormatting.RED));
				break;
			}
		}

		// Remove beacon
		player.level().setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
		return EventResult.DENY;
	}

	private EventResult onPlayerDamage(ServerPlayer player, DamageSource source, float amount) {
		PlayerEntry target = this.getEntryFromPlayer(player);
		if (target == null) return EventResult.PASS;

		PlayerEntry attacker = this.getEntryFromEntity(source.getEntity());

		// Prevent attacks from teammates
		if (attacker != null && target.isSameTeam(attacker)) {
			return EventResult.DENY;
		}

		// Dispatch handling to kits
		if (target.getKit() != null) {
			EventResult result = target.getKit().onDamaged(attacker, source, amount);
			if (result != EventResult.PASS) return result;
		}

		if (attacker != null && attacker.getKit() != null) {
			return attacker.getKit().onDealDamage(target, source, amount);
		}

		return EventResult.PASS;
	}
	
	public void onPlayerRemove(ServerPlayer player) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			this.eliminate(entry);
		}
	}

	@SuppressWarnings("deprecation")
	private EventResult onThrowItem(ServerPlayer player, int slot, ItemStack stack) {
		if (stack.getItem() instanceof BlockItem blockItem) {
			Holder<Block> entry = blockItem.getBlock().builtInRegistryHolder();
			
			if (entry.is(Main.RESPAWN_BEACONS)) {
				return EventResult.DENY;
			}
		}

		return EventResult.PASS;
	}

	public boolean isOldCombat() {
		return this.config.isOldCombat();
	}

	public KitType<?> getLayerKit() {
		return this.config.getLayerKit().orElse(null);
	}

	public static Vec3 getSpawnPos(ServerLevel world, MicroBattleMap map) {
		Vec3 center = map.getFloorBounds().center();
		int xOffset = (map.getRiverRadius() + 2) * (world.getRandom().nextBoolean() ? 1 : -1);
		int zOffset = (map.getRiverRadius() + 2) * (world.getRandom().nextBoolean() ? 1 : -1);

		return new Vec3(center.x() + xOffset + 0.5, map.getFloorBounds().max().getY(), center.z() + zOffset + 0.5);
	}

	public static void spawn(ServerLevel world, MicroBattleMap map, ServerPlayer player) {
		Vec3 spawnPos = MicroBattleActivePhase.getSpawnPos(world, map);
		player.teleportTo(world, spawnPos.x(), spawnPos.y(), spawnPos.z(), Set.of(), 0, 0, true);
	}
}