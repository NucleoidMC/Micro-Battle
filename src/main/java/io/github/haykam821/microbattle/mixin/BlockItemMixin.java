package io.github.haykam821.microbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.haykam821.microbattle.game.event.AfterBlockPlaceListener;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "postPlacement", at = @At("HEAD"))
	private void invokeAfterBlockPlaceListeners(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> ci) {
		if (world.isClient) return;
		
		try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			if (invokers.get(AfterBlockPlaceListener.EVENT).afterBlockPlace(pos, world, serverPlayer, stack, state) == EventResult.DENY) {
				world.setBlockState(pos, state.getFluidState().getBlockState());
				stack.increment(1);

				// Update inventory
				player.currentScreenHandler.sendContentUpdates();
				player.playerScreenHandler.onContentChanged(player.getInventory());
			}
		}
	}
}