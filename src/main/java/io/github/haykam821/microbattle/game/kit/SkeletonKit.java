package io.github.haykam821.microbattle.game.kit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class SkeletonKit extends ArcherKit {
	public SkeletonKit() {
		super(DyeColor.LIGHT_GRAY.getFireworkColor(), DyeColor.GRAY.getFireworkColor());
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.WOODEN_SWORD);
	}
}
