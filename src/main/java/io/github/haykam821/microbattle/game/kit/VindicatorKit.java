package io.github.haykam821.microbattle.game.kit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class VindicatorKit extends Kit {
	public VindicatorKit() {
		super(DyeColor.LIGHT_GRAY.getFireworkColor(), DyeColor.CYAN.getFireworkColor());
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.IRON_AXE);
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return null;
	}
}
