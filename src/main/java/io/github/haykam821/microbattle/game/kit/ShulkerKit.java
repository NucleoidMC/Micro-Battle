package io.github.haykam821.microbattle.game.kit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class ShulkerKit extends ArcherKit {
	public ShulkerKit() {
		super(DyeColor.MAGENTA.getFireworkColor(), DyeColor.MAGENTA.getFireworkColor());
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Your shulker arrows give others levitation",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You can only hold one arrow at a time",
			"Your arrows restock slowly",
		};
	}

	@Override
	protected int getArrowRestockDelay() {
		return 20 * 10;
	}

	@Override
	protected int getMaxArrows() {
		return 1;
	}

	@Override
	protected ItemStack getArrowStack() {
		ItemStack stack = ItemStackBuilder.of(Items.TIPPED_ARROW)
			.setName(new TranslatableText("item.microbattle.shulker_arrow"))
			.build();

		CompoundTag potionEffect = new CompoundTag();
		potionEffect.putInt("Id", 25);
		potionEffect.putInt("Duration", 3 * 20);

		ListTag customPotionEffects = new ListTag();
		customPotionEffects.add(potionEffect);

		stack.putSubTag("CustomPotionEffects", customPotionEffects);
		stack.getTag().putInt("CustomPotionColor", 0xCEFFFF);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.CHORUS_FRUIT, 8);
	}
}
