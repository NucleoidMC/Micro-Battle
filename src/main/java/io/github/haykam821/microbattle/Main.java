package io.github.haykam821.microbattle;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.KitPresets;
import io.github.haykam821.microbattle.game.kit.KitTypes;
import io.github.haykam821.microbattle.game.phase.MicroBattleWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.GameType;
import xyz.nucleoid.plasmid.game.rule.GameRule;

public class Main implements ModInitializer {
	public static final String MOD_ID = "microbattle";

	private static final Identifier MICRO_BATTLE_ID = new Identifier(MOD_ID, "micro_battle");
	public static final GameType<MicroBattleConfig> MICRO_BATTLE_TYPE = GameType.register(MICRO_BATTLE_ID, MicroBattleWaitingPhase::open, MicroBattleConfig.CODEC);

	private static final Identifier RESPAWN_BEACONS_ID = new Identifier(MOD_ID, "respawn_beacons");
	public static final Tag<Block> RESPAWN_BEACONS = TagRegistry.block(RESPAWN_BEACONS_ID);

	public static final GameRule FLUID_FLOW = new GameRule();

	@Override
	public void onInitialize() {
		KitTypes.initialize();
		KitPresets.initialize();
	}
}