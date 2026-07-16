package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import eu.pb4.sgui.api.SguiUtils;
import io.github.haykam821.microbattle.game.PlayerEntry;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

public class ShulkerKit extends ArcherKit {
	private static final Component ARROW_NAME = Component.translatable("item.microbattle.shulker_arrow").withStyle(SguiUtils.STYLE_CLEARER);

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
			.set(DataComponents.CUSTOM_NAME, ARROW_NAME)
			.build();

		Optional<Integer> customColor = Optional.of(0xCEFFFF);
		MobEffectInstance effect = new MobEffectInstance(MobEffects.LEVITATION, 3 * 20);

		PotionContents existingComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

		List<MobEffectInstance> customEffects = new ArrayList<>(existingComponent.customEffects());
		customEffects.add(effect);

		PotionContents component = new PotionContents(existingComponent.potion(), customColor, customEffects, existingComponent.customName());
		stack.set(DataComponents.POTION_CONTENTS, component);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.CHORUS_FRUIT, 8);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SHULKER_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SHULKER_HURT;
	}
}
