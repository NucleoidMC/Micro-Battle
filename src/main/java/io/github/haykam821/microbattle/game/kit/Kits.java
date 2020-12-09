package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.Main;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public enum Kits {
	FOX("fox", new FoxKit()),
	PLAYER("player", new Kit(DyeColor.RED.getFireworkColor(), DyeColor.RED.getFireworkColor())),
	SHULKER("shulker", new ShulkerKit()),
	SKELETON("skeleton", new SkeletonKit()),
	STRAY("stray", new StrayKit()),
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
