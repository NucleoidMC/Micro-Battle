package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.Main;
import net.minecraft.util.Identifier;

public enum Kits {
	SKELETON("skeleton", new SkeletonKit()),
	VINDICATOR("vindicator", new VindicatorKit());

	private final Identifier id;
	private final Kit kit;

	private Kits(String path, Kit kit) {
		this.id = new Identifier(Main.MOD_ID, path);
		this.kit = kit;
	}
	
	public Kit getKit() {
		return this.kit;
	}

	public static void register() {
		for (Kits kit : Kits.values()) {
			Kit.REGISTRY.register(kit.id, kit.kit);
		}
	}
}
