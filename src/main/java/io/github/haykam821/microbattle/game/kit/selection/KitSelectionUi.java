package io.github.haykam821.microbattle.game.kit.selection;

import io.github.haykam821.microbattle.game.kit.KitType;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.shop.ShopBuilder;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.shop.ShopUi;

public class KitSelectionUi {
	private static final Text TITLE = new TranslatableText("text.microbattle.kit_selection.title");
	private static final Text RANDOM_KIT = new TranslatableText("text.microbattle.kit_selection.random_kit").formatted(Formatting.LIGHT_PURPLE);

	private static void addKit(ShopBuilder builder, KitSelectionManager kitSelection, KitType<?> kitType) {
		Text name = kitType.getName().shallowCopy().formatted(Formatting.GREEN);

		builder.add(ShopEntry
			.ofIcon(Items.CHEST)
			.withName(name)
			.noCost()
			.onBuy(player -> {
				kitSelection.select(player, kitType);
			}));
	}

	public static ShopUi build(KitSelectionManager kitSelection, ServerPlayerEntity player) {
		return ShopUi.create(TITLE, builder -> {
			builder.add(ShopEntry
				.ofIcon(Items.ENDER_CHEST)
				.withName(RANDOM_KIT)
				.noCost()
				.onBuy(playerx -> {
					kitSelection.deselect(playerx);
				}));

			for (KitType<?> kitType : kitSelection.getKits()) {
				KitSelectionUi.addKit(builder, kitSelection, kitType);
			}
		});
	}
}
