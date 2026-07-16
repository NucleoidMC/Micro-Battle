package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import io.github.haykam821.microbattle.game.PlayerEntry;

public abstract class InventoryKit extends Kit {
	private final ItemStack[] stacks;

	public InventoryKit(KitType<?> type, PlayerEntry entry, ItemStack... stacks) {
		super(type, entry);
		this.stacks = stacks;
	}

	@Override
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		super.appendCustomInitialStacks(stacks);
		for (ItemStack stack : this.stacks) {
			stacks.add(stack.copy());
		}
	}
}
