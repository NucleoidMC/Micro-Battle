package io.github.haykam821.microbattle.game;

import io.github.haykam821.microbattle.game.kit.Kit;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEntry {
	private final ServerPlayerEntity player;
	private Kit kit;
	private int ticks = 0;

	public PlayerEntry(ServerPlayerEntity player) {
		this.player = player;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public int getTicks() {
		return this.ticks;
	}

	public void applyInventory() {
		if (this.kit != null) {
			this.kit.applyInventory(this.getPlayer());
		}
	}

	public void tick() {
		this.ticks += 1;
		if (this.kit != null) {
			this.kit.tick(this);
		}
	}
}
