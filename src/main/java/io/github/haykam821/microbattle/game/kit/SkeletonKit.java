package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

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
		return SoundEvents.ENTITY_SKELETON_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SKELETON_HURT;
	}
}
