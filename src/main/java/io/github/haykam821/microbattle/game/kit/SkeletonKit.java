package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkeletonKit extends ArcherKit {
	public SkeletonKit(KitType<?> type, PlayerEntry entry) {
		super(type, entry);
	}

	public SkeletonKit(PlayerEntry entry) {
		super(KitTypes.SKELETON, entry);
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.LIGHT_GRAY.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.GRAY.getFireworkColor();
	}

	@Override
	protected String[] getNeutrals() {
		return new String[] {
			"You can shoot others with arrows",
		};
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You can hold up to five arrows at a time",
			"Your arrows restock quickly",
		};
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.WOODEN_SWORD);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SKELETON_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SKELETON_HURT;
	}
}
