package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import xyz.nucleoid.stimuli.event.EventResult;

public class BlazeKit extends Kit {
	public BlazeKit(PlayerEntry entry) {
		super(KitTypes.BLAZE, entry);
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You are not damaged by fire",
			"When below half health or on fire, your attacks will engulf others in flames",
		};
	}
	
	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"Water is harmful to you",
		};
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.ORANGE.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.YELLOW.getFireworkColor();
	}

	@Override
	public boolean isDamagedByWater() {
		return true;
	}

	@Override
	public boolean isDamagedByFire() {
		return false;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.BLAZE_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BLAZE_HURT;
	}

	@Override
	public EventResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		if (this.player.isOnFire()) {
			target.getPlayer().igniteForSeconds((int) amount);
		}
		return EventResult.PASS;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.player.getHealth() < this.player.getMaxHealth() / 2 && this.player.getRemainingFireTicks() < 5) {
			this.player.setRemainingFireTicks(5);
		}
	}
}
