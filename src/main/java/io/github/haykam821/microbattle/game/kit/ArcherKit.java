package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public abstract class ArcherKit extends Kit {
	private final Item arrowItem = this.getArrowStack().getItem();

	public ArcherKit(KitType<?> type, PlayerEntry entry) {
		super(type, entry);
	}
	
	protected ItemStack getBowStack() {
		return ItemStackBuilder.of(Items.BOW)
			.setUnbreakable()
			.build();
	}

	protected int getArrowRestockDelay() {
		return 20 * 2;
	}

	protected int getMaxArrows() {
		return 5;
	}

	protected ItemStack getArrowStack() {
		return new ItemStack(Items.ARROW);
	}

	@Override
	protected void appendInitialStacks(List<ItemStack> stacks) {
		super.appendInitialStacks(stacks);
		stacks.add(this.getBowStack());
		stacks.add(this.getArrowStack());
	}

	private boolean canRestock(ServerPlayerEntity player) {
		return player.inventory.count(this.arrowItem) < this.getMaxArrows();
	}

	@Override
	public void tick(PlayerEntry entry) {
		if (entry.getTicks() % this.getArrowRestockDelay() == 0 && this.canRestock(entry.getPlayer())) {
			entry.getPlayer().giveItemStack(this.getArrowStack());
		}
	}

	protected static ItemStack potionArrowStack(Potion potion) {
		return potionLikeStack(Items.TIPPED_ARROW, potion);
	}
}
