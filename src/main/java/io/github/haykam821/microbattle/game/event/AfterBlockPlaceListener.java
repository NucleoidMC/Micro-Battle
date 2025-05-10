package io.github.haykam821.microbattle.game.event;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface AfterBlockPlaceListener {
	public StimulusEvent<AfterBlockPlaceListener> EVENT = StimulusEvent.create(AfterBlockPlaceListener.class, context -> {
		return (pos, world, player, stack, state) -> {
			try {
				for (AfterBlockPlaceListener listener : context.getListeners()) {
					EventResult result = listener.afterBlockPlace(pos, world, player, stack, state);
					if (result != EventResult.PASS) {
						return result;
					}
				}
			} catch (Throwable throwable) {
				context.handleException(throwable);
			}
			return EventResult.ALLOW;
		};
	});

	public EventResult afterBlockPlace(BlockPos pos, World world, ServerPlayerEntity player, ItemStack stack, BlockState state);
}
