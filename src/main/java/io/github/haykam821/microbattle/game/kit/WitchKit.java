package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class WitchKit extends Kit {
	public WitchKit(PlayerEntry entry) {
		super(KitTypes.WITCH, entry);
		this.addRestockEntry(new RestockEntry.Builder(this::getPotionStack, 20 * 10).maxCount(1).build(entry.getPlayer().registryAccess()));
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

	private ItemStack getPotionStack(HolderLookup.Provider registries) {
		return createPotionStack(Items.SPLASH_POTION, this.getPotion(registries));
	}

	private Optional<Holder<Potion>> getPotion(HolderLookup.Provider registries) {
		return registries
			.lookup(Registries.POTION)
			.flatMap(potions -> {
				List<Holder<Potion>> validPotions = potions
					.listElements()
					.filter(this::isValidPotion)
					.collect(Collectors.toUnmodifiableList());

				return Util.getRandomSafe(validPotions, RANDOM);
			});
	}

	private boolean isValidPotion(Holder<Potion> potion) {
		return !potion.value().getEffects().isEmpty();
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.WITCH_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.WITCH_HURT;
	}
}
