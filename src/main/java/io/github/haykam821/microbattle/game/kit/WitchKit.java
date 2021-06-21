package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class WitchKit extends Kit {
	public WitchKit(PlayerEntry entry) {
		super(KitTypes.WITCH, entry);
		this.addRestockEntry(new RestockEntry.Builder(this::getPotionStack, 20 * 10).maxCount(1).build());
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You can brew any kind of potion",
		};
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.PURPLE.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.BLACK.getFireworkColor();
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.WOODEN_SWORD);
	}

	@Override
	protected ItemStack getPickaxeToolStack() {
		return unbreakableStack(Items.WOODEN_PICKAXE);
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return unbreakableStack(Items.WOODEN_AXE);
	}

	@Override
	protected ItemStack getShovelToolStack() {
		return unbreakableStack(Items.WOODEN_SHOVEL);
	}

	private ItemStack getPotionStack() {
		return potionLikeStack(Items.SPLASH_POTION, Registry.POTION.getRandom(RANDOM));
	}
}
