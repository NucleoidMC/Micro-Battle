package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import net.minecraft.item.ItemStack;

public class InventoryKit extends Kit {
	private final ItemStack[] stacks;

	public InventoryKit(int baseColor, int secondaryColor, ItemStack... stacks) {
		super(baseColor, secondaryColor);
		this.stacks = stacks;
	}

	@Override
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		for (ItemStack stack : this.stacks) {
			stacks.add(stack.copy());
		}
	}
}
