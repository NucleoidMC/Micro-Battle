package io.github.haykam821.microbattle.game.kit;

import java.util.Arrays;
import java.util.List;

import io.github.haykam821.microbattle.Main;

public class KitPresets {
	public static final List<KitType<?>> RESPAWNER = register("respawner", KitTypes.RESPAWNER);
	public static final List<KitType<?>> SOUL = register("soul", new KitType[] {
		KitTypes.BEE,
		KitTypes.BLAZE,
		KitTypes.FOX,
		KitTypes.SHEEP,
		KitTypes.SHULKER,
		KitTypes.SKELETON,
		KitTypes.SNOW_GOLEM,
		KitTypes.STRAY,
		KitTypes.VINDICATOR,
		KitTypes.WITCH
	});
	public static final List<KitType<?>> STANDARD = register("standard", KitTypes.PLAYER);

	private static List<KitType<?>> register(String path, List<KitType<?>> types) {
		KitPreset.REGISTRY.register(Main.identifier(path), types);
		return types;
	}

	private static List<KitType<?>> register(String path, KitType<?>... types) {
		return register(path, Arrays.asList(types));
	}

	public static void initialize() {
		return;
	}
}
