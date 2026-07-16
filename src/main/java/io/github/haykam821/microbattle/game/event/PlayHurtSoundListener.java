package io.github.haykam821.microbattle.game.event;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface PlayHurtSoundListener {
	public StimulusEvent<PlayHurtSoundListener> EVENT = StimulusEvent.create(PlayHurtSoundListener.class, context -> {
		return (entity, source, defaultSound) -> {
			try {
				for (PlayHurtSoundListener listener : context.getListeners()) {
					SoundEvent sound = listener.playHurtSound(entity, source, defaultSound);
					if (sound != null) {
						return sound;
					}
				}
			} catch (Throwable throwable) {
				context.handleException(throwable);
			}
			return defaultSound;
		};
	});

	public SoundEvent playHurtSound(LivingEntity entity, DamageSource source, SoundEvent defaultSound);
}
