package io.github.haykam821.microbattle.game.kit.selection;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SimpleGuiBuilder;
import io.github.haykam821.microbattle.game.kit.KitType;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.shop.ShopEntry;

public class KitSelectionUi {
	private static final Text TITLE = Text.translatable("text.microbattle.kit_selection.title");
	private static final Text RANDOM_KIT = Text.translatable("text.microbattle.kit_selection.random_kit").formatted(Formatting.LIGHT_PURPLE);

	private static void addKit(SimpleGuiBuilder builder, KitSelectionManager kitSelection, KitType<?> kitType) {
		Text name = kitType.getName().copy().formatted(Formatting.GREEN);

		builder.addSlot(ShopEntry
			.ofIcon(kitType.getIcon())
			.withName(name)
			.noCost()
			.onBuy(player -> {
				kitSelection.select(player, kitType);
			}));
	}

	public static SimpleGui build(KitSelectionManager kitSelection, ServerPlayerEntity player) {
		SimpleGuiBuilder builder = new SimpleGuiBuilder(ScreenHandlerType.GENERIC_9X5, false);

		builder.setTitle(TITLE);

		builder.addSlot(ShopEntry
			.ofIcon(Items.ENDER_CHEST)
			.withName(RANDOM_KIT)
			.noCost()
			.onBuy(playerx -> {
				kitSelection.deselect(playerx);
			}));

		for (KitType<?> kitType : kitSelection.getKits()) {
			KitSelectionUi.addKit(builder, kitSelection, kitType);
		}

		return builder.build(player);
	}
}
