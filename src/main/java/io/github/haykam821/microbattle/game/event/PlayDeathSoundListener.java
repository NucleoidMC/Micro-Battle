package io.github.haykam821.microbattle.game.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface PlayDeathSoundListener {
	public StimulusEvent<PlayDeathSoundListener> EVENT = StimulusEvent.create(PlayDeathSoundListener.class, context -> {
		return (entity, defaultSound) -> {
			try {
				for (PlayDeathSoundListener listener : context.getListeners()) {
					SoundEvent sound = listener.playDeathSound(entity, defaultSound);
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

	public SoundEvent playDeathSound(LivingEntity entity, SoundEvent defaultSound);
}
