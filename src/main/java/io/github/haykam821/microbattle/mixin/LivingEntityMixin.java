package io.github.haykam821.microbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.haykam821.microbattle.game.event.PlayDeathSoundListener;
import io.github.haykam821.microbattle.game.event.PlayHurtSoundListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	protected abstract SoundEvent getDeathSound();

	@Shadow
	protected abstract SoundEvent getHurtSound(DamageSource source);

	@Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeathSound()Lnet/minecraft/sounds/SoundEvent;"))
	private SoundEvent modifyDeathSound(LivingEntity entity) {
		SoundEvent defaultSound = this.getDeathSound();
		if (entity.level().isClientSide()) return defaultSound;

		try (EventInvokers invokers = Stimuli.select().forEntity(entity)) {
			return invokers.get(PlayDeathSoundListener.EVENT).playDeathSound(entity, defaultSound);
		}
	}

	@Redirect(method = "playHurtSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;"))
	private SoundEvent modifyHurtSound(LivingEntity entity, DamageSource source) {
		SoundEvent defaultSound = this.getHurtSound(source);
		if (entity.level().isClientSide()) return defaultSound;

		try (EventInvokers invokers = Stimuli.select().forEntity(entity)) {
			return invokers.get(PlayHurtSoundListener.EVENT).playHurtSound(entity, source, defaultSound);
		}
	}
}
