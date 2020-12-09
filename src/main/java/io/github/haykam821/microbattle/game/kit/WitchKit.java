package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Random;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class WitchKit extends Kit {
	private static final Random RANDOM = new Random();
	private static final int MAX_RESTOCK_TICKS = 20 * 10;

	private int restockTicks = MAX_RESTOCK_TICKS;

	public WitchKit(PlayerEntry entry) {
		super(KitTypes.WITCH, entry);
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
		return new ItemStack(Items.WOODEN_SWORD);
	}

	@Override
	protected ItemStack getPickaxeToolStack() {
		return new ItemStack(Items.WOODEN_PICKAXE);
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return new ItemStack(Items.WOODEN_AXE);
	}

	@Override
	protected ItemStack getShovelToolStack() {
		return new ItemStack(Items.WOODEN_SHOVEL);
	}

	private ItemStack getPotionStack() {
		return potionLikeStack(Items.SPLASH_POTION, Registry.POTION.getRandom(RANDOM));
	}

	@Override
	protected void appendInitialStacks(List<ItemStack> stacks) {
		super.appendInitialStacks(stacks);
		stacks.add(this.getPotionStack());
	}

	@Override
	public void tick(PlayerEntry entry) {
		this.restockTicks -= 1;
		if (this.restockTicks <= 0 && this.player.inventory.count(Items.SPLASH_POTION) == 0) {
			this.restockTicks = MAX_RESTOCK_TICKS;
			this.player.giveItemStack(this.getPotionStack());
		}
	}
}
