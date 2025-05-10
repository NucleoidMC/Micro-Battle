package io.github.haykam821.microbattle.game.kit.selection;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import io.github.haykam821.microbattle.game.kit.KitType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import xyz.nucleoid.plasmid.api.shop.ShopEntry;

public class KitSelectionUi {
	private static final Text TITLE = Text.translatable("text.microbattle.kit_selection.title");
	private static final Text RANDOM_KIT = Text.translatable("text.microbattle.kit_selection.random_kit").formatted(Formatting.LIGHT_PURPLE);

	private static void addKit(SlotGuiInterface builder, KitSelectionManager kitSelection, KitType<?> kitType) {
		Text name = kitType.getName().copy().formatted(Formatting.GREEN);

		ItemStack icon = kitType.getIcon();
		icon.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

		if (icon.contains(DataComponentTypes.POTION_CONTENTS)) {
			icon.set(DataComponentTypes.CUSTOM_NAME, name.copy().styled(GuiHelpers.STYLE_CLEARER));
		}

		builder.addSlot(ShopEntry
			.ofIcon(icon)
			.withName(name)
			.noCost()
			.onBuy(player -> {
				kitSelection.select(player, kitType);
			}));
	}

	public static GuiInterface build(KitSelectionManager kitSelection, GuiInterface ui, ServerPlayerEntity player) {
		SlotGuiInterface gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
			@Override
			public void onClose() {
				super.onClose();
				ui.open();
			}
		};

		gui.setTitle(TITLE);

		ItemStack icon = new ItemStack(Items.ENDER_CHEST);
		icon.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

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
