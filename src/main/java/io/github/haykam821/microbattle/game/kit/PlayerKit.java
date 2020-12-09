package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.util.DyeColor;

public class PlayerKit extends Kit {
	public PlayerKit(PlayerEntry entry) {
		super(KitTypes.PLAYER, entry);
	}
	
	@Override
	protected int getBaseColor() {
		return DyeColor.RED.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.RED.getFireworkColor();
	}
}
