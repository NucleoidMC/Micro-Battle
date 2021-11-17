package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.util.DyeColor;

public class PlayerKit extends Kit {
	private final int color;

	public PlayerKit(KitType<?> type, PlayerEntry entry) {
		super(type, entry);
		this.color = this.entry.getTeamKey() == null ? DyeColor.RED.getFireworkColor() : this.entry.getTeamConfig().fireworkColor().getRgb();
	}

	public PlayerKit(PlayerEntry entry) {
		this(KitTypes.PLAYER, entry);
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
