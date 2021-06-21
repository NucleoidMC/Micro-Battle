package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

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
		return SoundEvents.ENTITY_VINDICATOR_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_VINDICATOR_HURT;
	}
}
