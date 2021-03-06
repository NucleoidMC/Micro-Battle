package io.github.haykam821.microbattle.game.kit.selection;

import eu.pb4.polymer.item.VirtualItem;
import io.github.haykam821.microbattle.game.event.OpenKitSelectionListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

public class KitSelectorItem extends Item implements VirtualItem {
	public KitSelectorItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world.isClient()) {
			return TypedActionResult.success(stack);
		}

		ManagedGameSpace gameSpace = ManagedGameSpace.forWorld(world);
		if (gameSpace == null) {
			return TypedActionResult.pass(stack);
		}
		
		ServerPlayerEntity serverUser = (ServerPlayerEntity) user;
		ActionResult result = gameSpace.invoker(OpenKitSelectionListener.EVENT).openKitSelection(world, serverUser, hand);

		return new TypedActionResult<>(result, stack);
	}

	@Override
	public Item getVirtualItem() {
		return Items.CHEST;
	}
}
