package io.github.haykam821.microbattle;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.KitPresets;
import io.github.haykam821.microbattle.game.kit.KitTypes;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectorItem;
import io.github.haykam821.microbattle.game.phase.MicroBattleWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "microbattle";

	private static final Identifier MICRO_BATTLE_ID = new Identifier(MOD_ID, "micro_battle");
	public static final GameType<MicroBattleConfig> MICRO_BATTLE_TYPE = GameType.register(MICRO_BATTLE_ID, MicroBattleConfig.CODEC, MicroBattleWaitingPhase::open);

	private static final Identifier KIT_SELECTOR_ID = new Identifier(MOD_ID, "kit_selector");
	public static final Item KIT_SELECTOR = new KitSelectorItem(new Item.Settings().maxCount(1));

	private static final Identifier RESPAWN_BEACONS_ID = new Identifier(MOD_ID, "respawn_beacons");
	public static final TagKey<Block> RESPAWN_BEACONS = TagKey.of(RegistryKeys.BLOCK, RESPAWN_BEACONS_ID);

	private static final Identifier POTENTIAL_BIOMES_ID = new Identifier(MOD_ID, "potential_biomes");
	public static final TagKey<Biome> POTENTIAL_BIOMES = TagKey.of(RegistryKeys.BIOME, POTENTIAL_BIOMES_ID);

	@Override
	public void onInitialize() {
		KitTypes.initialize();
		KitPresets.initialize();

		Registry.register(Registries.ITEM, KIT_SELECTOR_ID, KIT_SELECTOR);
	}
}