package io.github.haykam821.microbattle.game.kit.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import io.github.haykam821.microbattle.game.kit.KitType;

public class KitSelectionManager {
	private final List<KitType<?>> kits;
	private final Map<ServerPlayer, KitType<?>> selections = new HashMap<>();

	public KitSelectionManager(List<KitType<?>> kits) {
		this.kits = kits;
	}

	public List<KitType<?>> getKits() {
		return this.kits;
	}

	public boolean isKitSelectorNecessary() {
		return this.kits.size() > 1;
	}

	public KitType<?> get(ServerPlayer player, RandomSource random) {
		KitType<?> selection = this.selections.get(player);
		if (selection != null) return selection;

		return kits.get(random.nextInt(kits.size()));
	}

	public void select(ServerPlayer player, KitType<?> selection) {
		if (!this.kits.contains(selection)) {
			throw new IllegalStateException("Cannot select unselectable kit: " + selection);
		}
		this.selections.put(player, selection);
	}

	public void deselect(ServerPlayer player) {
		this.selections.remove(player);
	}
}
