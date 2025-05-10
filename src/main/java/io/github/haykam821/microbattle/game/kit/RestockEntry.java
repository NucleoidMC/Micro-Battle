package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;

public class RestockEntry {
	private final Function<RegistryWrapper.WrapperLookup, ItemStack> supplier;
	private final Item item;
	private final int maxTicks;
	private int ticks;
	private final int maxCount;

	private RestockEntry(RegistryWrapper.WrapperLookup registries, Function<RegistryWrapper.WrapperLookup, ItemStack> supplier, int maxTicks, int maxCount) {
		this.supplier = supplier;
		this.item = this.supplyStack(registries).getItem();

		this.maxTicks = maxTicks;
		this.ticks = maxTicks;

		this.maxCount = maxCount;
	}

	private boolean canSupplyTo(ServerPlayerEntity player) {
		return this.maxCount < 0 || player.getInventory().count(this.item) < this.maxCount;
	}

	public ItemStack supplyStack(RegistryWrapper.WrapperLookup registries) {
		return this.supplier.apply(registries);
	}

	public void tick(PlayerEntry entry) {
		this.ticks -= 1;
		if (this.ticks <= 0 && this.canSupplyTo(entry.getPlayer())) {
			this.ticks = this.maxTicks;
			entry.getPlayer().giveItemStack(this.supplyStack(entry.getPlayer().getRegistryManager()));
		}
	}
	
	public static class Builder {
		private final Function<RegistryWrapper.WrapperLookup, ItemStack> supplier;
		private final int maxTicks;
		private int maxCount = -1;

		public Builder(Function<RegistryWrapper.WrapperLookup, ItemStack> supplier, int maxTicks) {
			this.supplier = supplier;
			this.maxTicks = maxTicks;
		}

		public Builder(ItemConvertible item, int maxTicks) {
			this(registries -> new ItemStack(item), maxTicks);
		}

		public Builder maxCount(int maxCount) {
			this.maxCount = maxCount;
			return this;
		}

		public RestockEntry build(RegistryWrapper.WrapperLookup registries) {
			return new RestockEntry(registries, this.supplier, this.maxTicks, this.maxCount);
		}
	}
}
