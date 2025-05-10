package io.github.haykam821.microbattle;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.KitPresets;
import io.github.haykam821.microbattle.game.kit.KitTypes;
import io.github.haykam821.microbattle.game.phase.MicroBattleWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import xyz.nucleoid.plasmid.api.game.GameType;

public class Main implements ModInitializer {
	private static final String MOD_ID = "microbattle";

	private static final Identifier MICRO_BATTLE_ID = identifier("micro_battle");
	public static final GameType<MicroBattleConfig> MICRO_BATTLE_TYPE = GameType.register(MICRO_BATTLE_ID, MicroBattleConfig.CODEC, MicroBattleWaitingPhase::open);

	private static final Identifier RESPAWN_BEACONS_ID = identifier("respawn_beacons");
	public static final TagKey<Block> RESPAWN_BEACONS = TagKey.of(RegistryKeys.BLOCK, RESPAWN_BEACONS_ID);

	private static final Identifier POTENTIAL_BIOMES_ID = identifier("potential_biomes");
	public static final TagKey<Biome> POTENTIAL_BIOMES = TagKey.of(RegistryKeys.BIOME, POTENTIAL_BIOMES_ID);

	@Override
	public void onInitialize() {
		KitTypes.initialize();
		KitPresets.initialize();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}