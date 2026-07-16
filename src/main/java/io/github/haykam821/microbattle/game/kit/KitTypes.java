package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;

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
	public static final KitType<StrayKit> STRAY = register("stray", StrayKit::new, createPotion(Items.TIPPED_ARROW, Potions.SLOWNESS));
	public static final KitType<VindicatorKit> VINDICATOR = register("vindicator", VindicatorKit::new, Items.IRON_AXE);
	public static final KitType<WitchKit> WITCH = register("witch", WitchKit::new, createPotion(Items.SPLASH_POTION, Potions.HARMING));

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator, ItemStackTemplate icon) {
		KitType<T> type = new KitType<>(creator, icon);
		KitType.REGISTRY.register(Main.identifier(path), type);
		return type;
	}

	private static <T extends Kit> KitType<T> register(String path, Function<PlayerEntry, T> creator, ItemLike icon) {
		return register(path, creator, new ItemStackTemplate(icon.asItem()));
	}

	private static ItemStackTemplate createPotion(final Item item, final Holder<Potion> potion) {
		return new ItemStackTemplate(item, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potion)).build());
	}
	
	public static void initialize() {
		return;
	}
}
