package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;

public class StrayKit extends SkeletonKit {
	public StrayKit(PlayerEntry entry) {
		super(KitTypes.STRAY, entry);
	}

	@Override
	protected int getBaseColor() {
		return 0xD0DFEF;
	}

	@Override
	protected int getSecondaryColor() {
		return 0x799B9E;
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
			"Your arrows restock at a slightly slower rate",
			"Your arrows give others slowness",
			"You have a faster shovel",
		};
	}

	@Override
	protected int getArrowRestockDelay() {
		return 20 * 4;
	}

	@Override
	protected ItemStack getShovelToolStack() {
		return unbreakableStack(Items.IRON_SHOVEL);
	}

	@Override
	protected ItemStack getArrowStack() {
		return potionArrowStack(Potions.SLOWNESS);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.STRAY_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.STRAY_HURT;
	}
}
