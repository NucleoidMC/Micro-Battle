package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

public class StrayKit extends SkeletonKit {
	public StrayKit(PlayerEntry entry) {
		super(KitTypes.STRAY, entry);
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
		return SoundEvents.ENTITY_STRAY_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_STRAY_HURT;
	}
}
