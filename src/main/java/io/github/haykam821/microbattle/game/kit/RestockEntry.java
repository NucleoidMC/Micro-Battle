package io.github.haykam821.microbattle.game.kit;

import java.util.function.Supplier;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class RestockEntry {
	private final Supplier<ItemStack> supplier;
	private final Item item;
	private final int maxTicks;
	private int ticks;
	private final int maxCount;

	private RestockEntry(Supplier<ItemStack> supplier, int maxTicks, int maxCount) {
		this.supplier = supplier;
		this.item = this.supplyStack().getItem();

		this.maxTicks = maxTicks;
		this.ticks = maxTicks;

		this.maxCount = maxCount;
	}

	private boolean canSupplyTo(ServerPlayerEntity player) {
		return this.maxCount < 0 || player.inventory.count(this.item) < this.maxCount;
	}

	public ItemStack supplyStack() {
		return this.supplier.get();
	}

	public void tick(PlayerEntry entry) {
		this.ticks -= 1;
		if (this.ticks <= 0 && this.canSupplyTo(entry.getPlayer())) {
			this.ticks = this.maxTicks;
			entry.getPlayer().giveItemStack(this.supplyStack());
		}
	}
	
	public static class Builder {
		private final Supplier<ItemStack> supplier;
		private final int maxTicks;
		private int maxCount = -1;

		public Builder(Supplier<ItemStack> supplier, int maxTicks) {
			this.supplier = supplier;
			this.maxTicks = maxTicks;
		}

		public Builder(ItemConvertible item, int maxTicks) {
			this(() -> new ItemStack(item), maxTicks);
		}

		public Builder maxCount(int maxCount) {
			this.maxCount = maxCount;
			return this;
		}

		public RestockEntry build() {
			return new RestockEntry(this.supplier, this.maxTicks, this.maxCount);
		}
	}
}
