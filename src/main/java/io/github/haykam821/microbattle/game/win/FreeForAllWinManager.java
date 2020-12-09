package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class FreeForAllWinManager extends WinManager {
	public FreeForAllWinManager(MicroBattleActivePhase phase) {
		super(phase);
	}

	private Text getEndingMessage() {
		if (this.phase.getPlayers().size() == 1) {
			PlayerEntity winner = this.phase.getPlayers().iterator().next().getPlayer();
			return new TranslatableText("text.microbattle.win", winner.getDisplayName()).formatted(Formatting.GOLD);
		}
		return this.getNoWinnersMessage();
	}

	@Override
	public boolean checkForWinner() {
		if (this.phase.getPlayers().size() < 2) {
			if (this.phase.getPlayers().size() != 1 || !this.phase.isSingleplayer()) {
				this.phase.getGameSpace().getPlayers().sendMessage(this.getEndingMessage());
				return true;
			}
		}
		return false;
	}
}
