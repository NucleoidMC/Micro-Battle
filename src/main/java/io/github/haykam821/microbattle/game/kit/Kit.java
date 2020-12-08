package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class Kit {
	public static final TinyRegistry<Kit> REGISTRY = TinyRegistry.newStable();

	private final int baseColor;
	private final int secondaryColor;

	public Kit(int baseColor, int secondaryColor) {
		this.baseColor = baseColor;
		this.secondaryColor = secondaryColor;
	}

	private Text getName() {
		Identifier id = Kit.REGISTRY.getIdentifier(this);
		return new TranslatableText("kit." + id.getNamespace() + "." + id.getPath());
	}

	private ItemStack createArmorStack(Item item, String type, boolean secondary) {
		return ItemStackBuilder.of(item)
			.addEnchantment(Enchantments.BINDING_CURSE, 1)
			.setColor(secondary ? this.secondaryColor : this.baseColor)
			.setName(new TranslatableText("text.microbattle.team_armor." + type, this.getName()))
			.build();
	}

	private List<ItemStack> getArmorStacks() {
		List<ItemStack> armorStacks = new ArrayList<>();
		armorStacks.add(this.createArmorStack(Items.LEATHER_HELMET, "helmet", true));
		armorStacks.add(this.createArmorStack(Items.LEATHER_CHESTPLATE, "chestplate", false));
		armorStacks.add(this.createArmorStack(Items.LEATHER_LEGGINGS, "leggings", false));
		armorStacks.add(this.createArmorStack(Items.LEATHER_BOOTS, "boots", true));
		return armorStacks;
	}

	public void tick(PlayerEntry entry) {
		return;
	}

	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		return;
	}

	public final void applyInventory(ServerPlayerEntity player) {
		List<ItemStack> armorStacks = this.getArmorStacks();
		int index = 3;
		for (ItemStack stack : armorStacks) {
			player.inventory.armor.set(index, stack);
			index -= 1;
		}
	
		List<ItemStack> stacks = new ArrayList<>();
		this.appendCustomInitialStacks(stacks);
		int slot = 0;
		for (ItemStack stack : stacks) {
			player.inventory.setStack(slot, stack);
			slot += 1;
		}

		// Update inventory
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}
}
