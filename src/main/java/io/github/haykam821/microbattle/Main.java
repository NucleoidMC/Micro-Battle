package io.github.haykam821.microbattle;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.KitPresets;
import io.github.haykam821.microbattle.game.kit.KitTypes;
import io.github.haykam821.microbattle.game.phase.MicroBattleWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.GameTypes;

public class Main implements ModInitializer {
	private static final String MOD_ID = "microbattle";

	private static final Identifier MICRO_BATTLE_ID = identifier("micro_battle");
	public static final GameType<MicroBattleConfig> MICRO_BATTLE_TYPE = GameTypes.register(MICRO_BATTLE_ID, MicroBattleConfig.CODEC, MicroBattleWaitingPhase::open);

	private static final Identifier RESPAWN_BEACONS_ID = identifier("respawn_beacons");
	public static final TagKey<Block> RESPAWN_BEACONS = TagKey.create(Registries.BLOCK, RESPAWN_BEACONS_ID);

	private static final Identifier POTENTIAL_BIOMES_ID = identifier("potential_biomes");
	public static final TagKey<Biome> POTENTIAL_BIOMES = TagKey.create(Registries.BIOME, POTENTIAL_BIOMES_ID);

	@Override
	public void onInitialize() {
		KitTypes.initialize();
		KitPresets.initialize();
	}

	public static Identifier identifier(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}