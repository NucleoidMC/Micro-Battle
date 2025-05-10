package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;

public class WitchKit extends Kit {
	public WitchKit(PlayerEntry entry) {
		super(KitTypes.WITCH, entry);
		this.addRestockEntry(new RestockEntry.Builder(this::getPotionStack, 20 * 10).maxCount(1).build(entry.getPlayer().getRegistryManager()));
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

	private ItemStack getPotionStack(RegistryWrapper.WrapperLookup registries) {
		return createPotionStack(Items.SPLASH_POTION, this.getPotion(registries));
	}

	private Optional<RegistryEntry<Potion>> getPotion(RegistryWrapper.WrapperLookup registries) {
		return registries
			.getOptional(RegistryKeys.POTION)
			.flatMap(potions -> {
				List<RegistryEntry<Potion>> validPotions = potions
					.streamEntries()
					.filter(this::isValidPotion)
					.collect(Collectors.toUnmodifiableList());

				return Util.getRandomOrEmpty(validPotions, RANDOM);
			});
	}

	private boolean isValidPotion(RegistryEntry<Potion> potion) {
		return !potion.value().getEffects().isEmpty();
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITCH_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITCH_HURT;
	}
}
