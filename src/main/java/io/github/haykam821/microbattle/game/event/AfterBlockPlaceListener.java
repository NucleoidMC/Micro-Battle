package io.github.haykam821.microbattle.game.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

	public EventResult afterBlockPlace(BlockPos pos, Level world, ServerPlayer player, ItemStack stack, BlockState state);
}
