package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class VindicatorKit extends Kit {
	public VindicatorKit() {
		super(DyeColor.LIGHT_GRAY.getFireworkColor(), DyeColor.CYAN.getFireworkColor());
	}
	
	@Override
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		stacks.add(ItemStackBuilder.of(Items.IRON_AXE).setUnbreakable().build());
	}
}
