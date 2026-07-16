package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import io.github.haykam821.microbattle.game.PlayerEntry;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

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
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		super.appendCustomInitialStacks(stacks);
		stacks.add(this.getBowStack());
		stacks.add(this.getArrowStack());
	}

	private boolean canRestock(ServerPlayer player) {
		return player.getInventory().countItem(this.arrowItem) < this.getMaxArrows();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.entry.getTicks() % this.getArrowRestockDelay() == 0 && this.canRestock(this.player)) {
			this.player.addItem(this.getArrowStack());
		}
	}

	protected static ItemStack potionArrowStack(Holder<Potion> potion) {
		return PotionContents.createItemStack(Items.TIPPED_ARROW, potion);
	}
}
