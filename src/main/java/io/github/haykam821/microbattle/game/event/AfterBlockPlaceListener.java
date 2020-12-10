package io.github.haykam821.microbattle.game.event;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.event.EventType;

public interface AfterBlockPlaceListener {
	public EventType<AfterBlockPlaceListener> EVENT = EventType.create(AfterBlockPlaceListener.class, listeners -> {
		return (pos, world, player, stack, state) -> {
			for (AfterBlockPlaceListener listener : listeners) {
				ActionResult result = listener.afterBlockPlace(pos, world, player, stack, state);
				if (result != ActionResult.PASS) {
					return result;
				}
			}
			return ActionResult.SUCCESS;
		};
	});

	public ActionResult afterBlockPlace(BlockPos pos, World world, ServerPlayerEntity player, ItemStack stack, BlockState state);
}
