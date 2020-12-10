package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.util.Identifier;

public class KitTypes {
	public static final KitType<BlazeKit> BLAZE = register("blaze", BlazeKit::new);
	public static final KitType<FoxKit> FOX = register("fox", FoxKit::new);
	public static final KitType<PlayerKit> PLAYER = register("player", PlayerKit::new);
	public static final KitType<RespawnerKit> RESPAWNER = register("respawner", RespawnerKit::new);
	public static final KitType<SheepKit> SHEEP = register("sheep", SheepKit::new);
	public static final KitType<ShulkerKit> SHULKER = register("shulker", ShulkerKit::new);
	public static final KitType<SkeletonKit> SKELETON = register("skeleton", SkeletonKit::new);
	public static final KitType<SnowGolemKit> SNOW_GOLEM = register("snow_golem", SnowGolemKit::new);
	public static final KitType<StrayKit> STRAY = register("stray", StrayKit::new);
	public static final KitType<VindicatorKit> VINDICATOR = register("vindicator", VindicatorKit::new);
	public static final KitType<WitchKit> WITCH = register("witch", WitchKit::new);

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator) {
		KitType<T> type = new KitType<>(creator);
		KitType.REGISTRY.register(new Identifier(Main.MOD_ID, path), type);
		return type;
	}
	
	public static void initialize() {
		return;
	}
}
