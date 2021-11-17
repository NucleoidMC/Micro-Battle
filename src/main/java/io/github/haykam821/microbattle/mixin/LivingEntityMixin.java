package io.github.haykam821.microbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.haykam821.microbattle.game.event.PlayDeathSoundListener;
import io.github.haykam821.microbattle.game.event.PlayHurtSoundListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	protected abstract SoundEvent getDeathSound();

	@Shadow
	protected abstract SoundEvent getHurtSound(DamageSource source);

	@Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getDeathSound()Lnet/minecraft/sound/SoundEvent;"))
	private SoundEvent modifyDeathSound(LivingEntity entity) {
		SoundEvent defaultSound = this.getDeathSound();
		if (entity.getEntityWorld().isClient()) return defaultSound;

		try (EventInvokers invokers = Stimuli.select().forEntity(entity)) {
			return invokers.get(PlayDeathSoundListener.EVENT).playDeathSound(entity, defaultSound);
		}
	}

	@Redirect(method = "playHurtSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;"))
	private SoundEvent modifyHurtSound(LivingEntity entity, DamageSource source) {
		SoundEvent defaultSound = this.getHurtSound(source);
		if (entity.getEntityWorld().isClient()) return defaultSound;

		try (EventInvokers invokers = Stimuli.select().forEntity(entity)) {
			return invokers.get(PlayHurtSoundListener.EVENT).playHurtSound(entity, source, defaultSound);
		}
	}
}
