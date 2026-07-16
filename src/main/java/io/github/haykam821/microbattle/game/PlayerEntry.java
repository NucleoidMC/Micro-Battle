package io.github.haykam821.microbattle.game;

import io.github.haykam821.microbattle.game.kit.Kit;
import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamKey;

public class PlayerEntry {
	private final MicroBattleActivePhase phase;
	private final ServerPlayer player;
	private final GameTeamKey teamKey;
	private final Kit kit;
	private int ticks = 0;
	private int outOfBoundsTicks = 0;

	public PlayerEntry(MicroBattleActivePhase phase, ServerPlayer player, GameTeamKey teamKey, KitType<?> kitType) {
		this.phase = phase;
		this.player = player;
		this.teamKey = teamKey;
		this.kit = kitType.create(this, phase.getLayerKit());
	}

	public MicroBattleActivePhase getPhase() {
		return this.phase;
	}

	public ServerPlayer getPlayer() {
		return this.player;
	}

	public GameTeamKey getTeamKey() {
		return this.teamKey;
	}
	
	public GameTeamConfig getTeamConfig() {
		return this.phase.getTeamConfig(this.teamKey);
	}

	public boolean isSameTeam(PlayerEntry other) {
		if (this.teamKey == null) return false;
		if (other.teamKey == null) return false;
		return this.teamKey == other.teamKey;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void initializeKit() {
		this.kit.initialize();
	}

	public int getTicks() {
		return this.ticks;
	}

	public void tick() {
		this.ticks += 1;
		if (this.kit != null) {
			this.kit.baseTick();
		}
	}

	public void tickOutOfBounds() {
		this.outOfBoundsTicks += 1;
		this.player.hurtServer(this.player.level(), this.player.damageSources().fellOutOfWorld(), this.outOfBoundsTicks / 80);
	}

	/**
	 * Sends inventory updates to the player's client.
	 */
	public void updateInventory() {
		this.player.containerMenu.broadcastChanges();
		this.player.inventoryMenu.slotsChanged(this.player.getInventory());
	}

	public void onEliminated() {
		this.player.setGameMode(GameType.SPECTATOR);

		this.player.getInventory().clearContent();
		this.updateInventory();
	}
}
