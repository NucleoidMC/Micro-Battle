package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VindicatorKit extends Kit {
	public VindicatorKit(PlayerEntry entry) {
		super(KitTypes.VINDICATOR, entry);
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.LIGHT_GRAY.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.CYAN.getFireworkColor();
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Your axe is stronger"
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You have no sword"
		};
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.IRON_AXE);
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return null;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.COOKED_BEEF, 4);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.VINDICATOR_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.VINDICATOR_HURT;
	}
}
