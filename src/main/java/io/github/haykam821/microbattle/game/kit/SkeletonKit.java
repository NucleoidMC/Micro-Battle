package io.github.haykam821.microbattle.game.kit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class SkeletonKit extends ArcherKit {
	public SkeletonKit() {
		super(DyeColor.LIGHT_GRAY.getFireworkColor(), DyeColor.GRAY.getFireworkColor());
	}

	@Override
	protected String[] getNeutrals() {
		return new String[] {
			"You can shoot others with arrows",
		};
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You can hold up to five arrows at a time",
			"Your arrows restock quickly",
		};
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.WOODEN_SWORD);
	}
}
