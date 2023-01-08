package io.github.haykam821.microbattle.game.kit.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.haykam821.microbattle.game.kit.KitType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

public class KitSelectionManager {
	private final List<KitType<?>> kits;
	private final Map<ServerPlayerEntity, KitType<?>> selections = new HashMap<>();

	public KitSelectionManager(List<KitType<?>> kits) {
		this.kits = kits;
	}

	public List<KitType<?>> getKits() {
		return this.kits;
	}

	public boolean isKitSelectorNecessary() {
		return this.kits.size() > 1;
	}

	public KitType<?> get(ServerPlayerEntity player, Random random) {
		KitType<?> selection = this.selections.get(player);
		if (selection != null) return selection;

		return kits.get(random.nextInt(kits.size()));
	}

	public void select(ServerPlayerEntity player, KitType<?> selection) {
		if (!this.kits.contains(selection)) {
			throw new IllegalStateException("Cannot select unselectable kit: " + selection);
		}
		this.selections.put(player, selection);
	}

	public void deselect(ServerPlayerEntity player) {
		this.selections.remove(player);
	}
}
