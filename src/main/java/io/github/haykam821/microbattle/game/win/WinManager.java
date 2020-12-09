package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class WinManager {
	protected final MicroBattleActivePhase phase;

	public WinManager(MicroBattleActivePhase phase) {
		this.phase = phase;
	}

	protected Text getNoWinnersMessage() {
		return new TranslatableText("text.microbattle.no_winners").formatted(Formatting.GOLD);
	}

	public abstract boolean checkForWinner();
}
