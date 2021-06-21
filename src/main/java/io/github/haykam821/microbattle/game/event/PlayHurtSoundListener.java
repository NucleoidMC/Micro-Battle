package io.github.haykam821.microbattle.game.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import xyz.nucleoid.plasmid.game.event.EventType;

public interface PlayHurtSoundListener {
	public EventType<PlayHurtSoundListener> EVENT = EventType.create(PlayHurtSoundListener.class, listeners -> {
		return (entity, source, defaultSound) -> {
			for (PlayHurtSoundListener listener : listeners) {
				SoundEvent sound = listener.playHurtSound(entity, source, defaultSound);
				if (sound != null) {
					return sound;
				}
			}
			return defaultSound;
		};
	});

	public SoundEvent playHurtSound(LivingEntity entity, DamageSource source, SoundEvent defaultSound);
}
