package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;

public class KitTypes {
	public static final KitType<BeeKit> BEE = register("bee", BeeKit::new, Items.BEEHIVE);
	public static final KitType<BlazeKit> BLAZE = register("blaze", BlazeKit::new, Items.BLAZE_POWDER);
	public static final KitType<FoxKit> FOX = register("fox", FoxKit::new, Items.SWEET_BERRIES);
	public static final KitType<PlayerKit> PLAYER = register("player", PlayerKit::new, Items.PLAYER_HEAD);
	public static final KitType<RespawnerKit> RESPAWNER = register("respawner", RespawnerKit::new, Items.BEACON);
	public static final KitType<SheepKit> SHEEP = register("sheep", SheepKit::new, Items.SHORT_GRASS);
	public static final KitType<ShulkerKit> SHULKER = register("shulker", ShulkerKit::new, Items.SHULKER_SHELL);
	public static final KitType<SkeletonKit> SKELETON = register("skeleton", SkeletonKit::new, Items.BONE);
	public static final KitType<SnowGolemKit> SNOW_GOLEM = register("snow_golem", SnowGolemKit::new, Items.SNOWBALL);
	public static final KitType<StrayKit> STRAY = register("stray", StrayKit::new, PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.SLOWNESS));
	public static final KitType<VindicatorKit> VINDICATOR = register("vindicator", VindicatorKit::new, Items.IRON_AXE);
	public static final KitType<WitchKit> WITCH = register("witch", WitchKit::new, PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.HARMING));

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator, ItemStack icon) {
		KitType<T> type = new KitType<>(creator, icon);
		KitType.REGISTRY.register(Main.identifier(path), type);
		return type;
	}

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator, ItemConvertible icon) {
		return register(path, creator, new ItemStack(icon));
	}
	
	public static void initialize() {
		return;
	}
}
