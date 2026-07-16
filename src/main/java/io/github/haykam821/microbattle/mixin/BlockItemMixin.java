package io.github.haykam821.microbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.haykam821.microbattle.game.event.AfterBlockPlaceListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"))
	private void invokeAfterBlockPlaceListeners(BlockPos pos, Level world, Player player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> ci) {
		if (world.isClientSide()) return;
		
		try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
			ServerPlayer serverPlayer = (ServerPlayer) player;
			if (invokers.get(AfterBlockPlaceListener.EVENT).afterBlockPlace(pos, world, serverPlayer, stack, state) == EventResult.DENY) {
				world.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
				stack.grow(1);

				// Update inventory
				player.containerMenu.broadcastChanges();
				player.inventoryMenu.slotsChanged(player.getInventory());
			}
		}
	}
}