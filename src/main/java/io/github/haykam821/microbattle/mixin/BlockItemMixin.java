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
	@Inject(method = "updateCustomBlockEntityTag", at = @At("HEAD"))
	private static void invokeAfterBlockPlaceListeners(Level level, Player player, BlockPos pos, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (level.isClientSide()) return;
		
		try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
			ServerPlayer serverPlayer = (ServerPlayer) player;
			var state = level.getBlockState(pos);
			if (invokers.get(AfterBlockPlaceListener.EVENT).afterBlockPlace(pos, level, serverPlayer, itemStack, state) == EventResult.DENY) {
				level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
				itemStack.grow(1);

				// Update inventory
				player.containerMenu.broadcastChanges();
				player.inventoryMenu.slotsChanged(player.getInventory());
			}
		}
	}
}