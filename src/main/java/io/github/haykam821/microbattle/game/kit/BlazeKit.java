package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
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
		return SoundEvents.ENTITY_BLAZE_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BLAZE_HURT;
	}

	@Override
	public EventResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		if (this.player.isOnFire()) {
			target.getPlayer().setOnFireFor((int) amount);
		}
		return EventResult.PASS;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.player.getHealth() < this.player.getMaxHealth() / 2 && this.player.getFireTicks() < 5) {
			this.player.setFireTicks(5);
		}
	}
}
