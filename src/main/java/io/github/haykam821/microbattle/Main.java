package io.github.haykam821.microbattle;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.KitTypes;
import io.github.haykam821.microbattle.game.phase.MicroBattleWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "microbattle";

	private static final Identifier MICRO_BATTLE_ID = new Identifier(MOD_ID, "micro_battle");
	public static final GameType<MicroBattleConfig> MICRO_BATTLE_TYPE = GameType.register(MICRO_BATTLE_ID, MicroBattleWaitingPhase::open, MicroBattleConfig.CODEC);

	@Override
	public void onInitialize() {
		KitTypes.initialize();
	}
}