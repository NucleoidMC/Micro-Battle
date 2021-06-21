package io.github.haykam821.microbattle.game.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.event.EventType;

public interface OpenKitSelectionListener {
	public EventType<OpenKitSelectionListener> EVENT = EventType.create(OpenKitSelectionListener.class, listeners -> {
		return (world, user, hand) -> {
			for (OpenKitSelectionListener listener : listeners) {
				ActionResult result = listener.openKitSelection(world, user, hand);
				if (result != ActionResult.PASS) {
					return result;
				}
			}
			return ActionResult.SUCCESS;
		};
	});

	public ActionResult openKitSelection(World world, ServerPlayerEntity user, Hand hand);
}
