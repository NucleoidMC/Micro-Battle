package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public abstract class WinManager {
	protected final MicroBattleActivePhase phase;

	public WinManager(MicroBattleActivePhase phase) {
		this.phase = phase;
	}

	protected Component getNoWinnersMessage() {
		return Component.translatable("text.microbattle.no_winners").withStyle(ChatFormatting.GOLD);
	}

	public abstract boolean checkForWinner();
}
