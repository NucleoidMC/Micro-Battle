package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.util.Identifier;

public class KitTypes {
	public static KitType<FoxKit> FOX = register("fox", FoxKit::new);
	public static KitType<PlayerKit> PLAYER = register("player", PlayerKit::new);
	public static KitType<ShulkerKit> SHULKER = register("shulker", ShulkerKit::new);
	public static KitType<SkeletonKit> SKELETON = register("skeleton", SkeletonKit::new);
	public static KitType<StrayKit> STRAY = register("stray", StrayKit::new);
	public static KitType<VindicatorKit> VINDICATOR = register("vindicator", VindicatorKit::new);

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator) {
		KitType<T> type = new KitType<>(creator);
		KitType.REGISTRY.register(new Identifier(Main.MOD_ID, path), type);
		return type;
	}
	
	public static void initialize() {
		return;
	}
}
