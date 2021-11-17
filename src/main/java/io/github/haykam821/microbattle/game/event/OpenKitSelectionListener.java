package io.github.haykam821.microbattle.game.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface OpenKitSelectionListener {
	public StimulusEvent<OpenKitSelectionListener> EVENT = StimulusEvent.create(OpenKitSelectionListener.class, context -> {
		return (world, user, hand) -> {
			try {
				for (OpenKitSelectionListener listener : context.getListeners()) {
					ActionResult result = listener.openKitSelection(world, user, hand);
					if (result != ActionResult.PASS) {
						return result;
					}
				}
			} catch (Throwable throwable) {
				context.handleException(throwable);
			}
			return ActionResult.PASS;
		};
	});

	public ActionResult openKitSelection(World world, ServerPlayerEntity user, Hand hand);
}
