package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.util.DyeColor;

public class PlayerKit extends Kit {
	private final int color;

	public PlayerKit(PlayerEntry entry) {
		super(KitTypes.PLAYER, entry);
		this.color = this.entry.getTeam() == null ? DyeColor.RED.getFireworkColor() : this.entry.getTeam().getFireworkColor();
	}
	
	@Override
	protected int getBaseColor() {
		return this.color;
	}

	@Override
	protected int getSecondaryColor() {
		return this.color;
	}
}
