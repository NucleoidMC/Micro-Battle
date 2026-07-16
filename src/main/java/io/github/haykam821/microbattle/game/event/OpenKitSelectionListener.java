package io.github.haykam821.microbattle.game.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface OpenKitSelectionListener {
	public StimulusEvent<OpenKitSelectionListener> EVENT = StimulusEvent.create(OpenKitSelectionListener.class, context -> {
		return (world, user, hand) -> {
			try {
				for (OpenKitSelectionListener listener : context.getListeners()) {
					InteractionResult result = listener.openKitSelection(world, user, hand);
					if (result != InteractionResult.PASS) {
						return result;
					}
				}
			} catch (Throwable throwable) {
				context.handleException(throwable);
			}
			return InteractionResult.PASS;
		};
	});

	public InteractionResult openKitSelection(Level world, ServerPlayer user, InteractionHand hand);
}
