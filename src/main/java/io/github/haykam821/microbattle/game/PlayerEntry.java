package io.github.haykam821.microbattle.game;

import io.github.haykam821.microbattle.game.kit.Kit;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.player.GameTeam;

public class PlayerEntry {
	private final MicroBattleActivePhase phase;
	private final ServerPlayerEntity player;
	private final GameTeam team;
	private Kit kit;
	private int ticks = 0;
	private int outOfBoundsTicks = 0;

	public PlayerEntry(MicroBattleActivePhase phase, ServerPlayerEntity player, GameTeam team) {
		this.phase = phase;
		this.player = player;
		this.team = team;
	}

	public MicroBattleActivePhase getPhase() {
		return this.phase;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}
	
	public GameTeam getTeam() {
		return this.team;
	}

	public boolean isSameTeam(PlayerEntry other) {
		if (this.team == null) return false;
		if (other.team == null) return false;
		return this.team == other.team;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void initializeKit(Kit kit) {
		this.kit = kit;
		this.kit.initialize();
	}

	public int getTicks() {
		return this.ticks;
	}

	public void tick() {
		this.ticks += 1;
		if (this.kit != null) {
			this.kit.tick();
		}
	}

	public void tickOutOfBounds() {
		this.outOfBoundsTicks += 1;
		player.damage(DamageSource.OUT_OF_WORLD, this.outOfBoundsTicks / 80);
	}
}
