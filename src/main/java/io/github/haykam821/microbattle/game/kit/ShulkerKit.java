package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class ShulkerKit extends ArcherKit {
	public ShulkerKit(PlayerEntry entry) {
		super(KitTypes.SHULKER, entry);
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.MAGENTA.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.MAGENTA.getFireworkColor();
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

		NbtCompound potionEffect = new NbtCompound();
		potionEffect.putInt("Id", 25);
		potionEffect.putInt("Duration", 3 * 20);

		NbtList customPotionEffects = new NbtList();
		customPotionEffects.add(potionEffect);

		stack.putSubTag("CustomPotionEffects", customPotionEffects);
		stack.getTag().putInt("CustomPotionColor", 0xCEFFFF);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.CHORUS_FRUIT, 8);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SHULKER_HURT;
	}
}
