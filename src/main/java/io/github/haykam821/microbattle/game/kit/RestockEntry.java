package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import io.github.haykam821.microbattle.game.PlayerEntry;

public class RestockEntry {
	private final Function<HolderLookup.Provider, ItemStack> supplier;
	private final Item item;
	private final int maxTicks;
	private int ticks;
	private final int maxCount;

	private RestockEntry(HolderLookup.Provider registries, Function<HolderLookup.Provider, ItemStack> supplier, int maxTicks, int maxCount) {
		this.supplier = supplier;
		this.item = this.supplyStack(registries).getItem();

		this.maxTicks = maxTicks;
		this.ticks = maxTicks;

		this.maxCount = maxCount;
	}

	private boolean canSupplyTo(ServerPlayer player) {
		return this.maxCount < 0 || player.getInventory().countItem(this.item) < this.maxCount;
	}

	public ItemStack supplyStack(HolderLookup.Provider registries) {
		return this.supplier.apply(registries);
	}

	public void tick(PlayerEntry entry) {
		this.ticks -= 1;
		if (this.ticks <= 0 && this.canSupplyTo(entry.getPlayer())) {
			this.ticks = this.maxTicks;
			entry.getPlayer().addItem(this.supplyStack(entry.getPlayer().registryAccess()));
		}
	}
	
	public static class Builder {
		private final Function<HolderLookup.Provider, ItemStack> supplier;
		private final int maxTicks;
		private int maxCount = -1;

		public Builder(Function<HolderLookup.Provider, ItemStack> supplier, int maxTicks) {
			this.supplier = supplier;
			this.maxTicks = maxTicks;
		}

		public Builder(ItemLike item, int maxTicks) {
			this(registries -> new ItemStack(item), maxTicks);
		}

		public Builder maxCount(int maxCount) {
			this.maxCount = maxCount;
			return this;
		}

		public RestockEntry build(HolderLookup.Provider registries) {
			return new RestockEntry(registries, this.supplier, this.maxTicks, this.maxCount);
		}
	}
}
