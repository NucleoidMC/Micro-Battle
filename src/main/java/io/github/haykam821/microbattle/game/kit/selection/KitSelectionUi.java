package io.github.haykam821.microbattle.game.kit.selection;

import eu.pb4.sgui.api.SguiUtils;
import eu.pb4.sgui.api.gui.GuiLike;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotBasedGui;
import io.github.haykam821.microbattle.game.kit.KitType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import xyz.nucleoid.plasmid.api.shop.ShopEntry;

public class KitSelectionUi {
	private static final Component TITLE = Component.translatable("text.microbattle.kit_selection.title");
	private static final Component RANDOM_KIT = Component.translatable("text.microbattle.kit_selection.random_kit").withStyle(ChatFormatting.LIGHT_PURPLE);

	private static void addKit(SlotBasedGui builder, KitSelectionManager kitSelection, KitType<?> kitType) {
		Component name = kitType.getName().copy().withStyle(ChatFormatting.GREEN);

		ItemStack icon = kitType.getIcon();

		icon.update(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT, display -> {
			return display
				.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
				.withHidden(DataComponents.BEES, true)
				.withHidden(DataComponents.BLOCK_STATE, true)
				.withHidden(DataComponents.POTION_CONTENTS, true);
		});

		if (icon.has(DataComponents.POTION_CONTENTS)) {
			icon.set(DataComponents.CUSTOM_NAME, name.copy().withStyle(SguiUtils.STYLE_CLEARER));
		}

		builder.addSlot(ShopEntry
			.ofIcon(icon)
			.withName(name)
			.noCost()
			.onBuy(player -> {
				kitSelection.select(player, kitType);
			}));
	}

	public static GuiLike build(KitSelectionManager kitSelection, GuiLike ui, ServerPlayer player) {
		var gui = new SimpleGui(MenuType.GENERIC_9x5, player, false) {
			@Override
			public void onRemoved() {
				ui.open();
			}
		};

		gui.setTitle(TITLE);

		ItemStack icon = new ItemStack(Items.ENDER_CHEST);

		gui.addSlot(ShopEntry
			.ofIcon(icon)
			.withName(RANDOM_KIT)
			.noCost()
			.onBuy(playerx -> {
				kitSelection.deselect(playerx);
			}));

		for (KitType<?> kitType : kitSelection.getKits()) {
			KitSelectionUi.addKit(gui, kitSelection, kitType);
		}

		return gui;
	}
}
