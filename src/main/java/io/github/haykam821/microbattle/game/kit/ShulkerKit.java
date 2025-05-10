package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import eu.pb4.sgui.api.GuiHelpers;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

public class ShulkerKit extends ArcherKit {
	private static final Text ARROW_NAME = Text.translatable("item.microbattle.shulker_arrow").styled(GuiHelpers.STYLE_CLEARER);

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
			.set(DataComponentTypes.CUSTOM_NAME, ARROW_NAME)
			.build();

		Optional<Integer> customColor = Optional.of(0xCEFFFF);
		StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.LEVITATION, 3 * 20);

		PotionContentsComponent existingComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

		List<StatusEffectInstance> customEffects = new ArrayList<>(existingComponent.customEffects());
		customEffects.add(effect);

		PotionContentsComponent component = new PotionContentsComponent(existingComponent.potion(), customColor, customEffects, existingComponent.customName());
		stack.set(DataComponentTypes.POTION_CONTENTS, component);

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
