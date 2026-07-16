package io.github.haykam821.microbattle.game.kit.selection;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.game.common.ui.WaitingLobbyUiElement;

public class KitSelectionWaitingLobbyUiElement implements WaitingLobbyUiElement {
	private static final Component NAME = Component.translatable("text.microbattle.kit_selector");

	private final KitSelectionManager kitSelection;

	public KitSelectionWaitingLobbyUiElement(KitSelectionManager kitSelection) {
		this.kitSelection = kitSelection;
	}

	@Override
	public GuiElement createMainElement() {
		return new GuiElementBuilder(Items.CHEST)
			.setItemName(NAME)
			.setCallback((index, type, action, ui) -> {
				if (type.isRight) {
					KitSelectionUi.build(this.kitSelection, ui, ui.getPlayer()).open();
				}
			})
			.build();
	}
}
