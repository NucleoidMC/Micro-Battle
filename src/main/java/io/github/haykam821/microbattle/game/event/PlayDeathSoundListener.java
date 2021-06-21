package io.github.haykam821.microbattle.game.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;
import xyz.nucleoid.plasmid.game.event.EventType;

public interface PlayDeathSoundListener {
	public EventType<PlayDeathSoundListener> EVENT = EventType.create(PlayDeathSoundListener.class, listeners -> {
		return (entity, defaultSound) -> {
			for (PlayDeathSoundListener listener : listeners) {
				SoundEvent sound = listener.playDeathSound(entity, defaultSound);
				if (sound != null) {
					return sound;
				}
			}
			return defaultSound;
		};
	});

	public SoundEvent playDeathSound(LivingEntity entity, SoundEvent defaultSound);
}
