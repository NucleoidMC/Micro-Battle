package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class FreeForAllWinManager extends WinManager {
	public FreeForAllWinManager(MicroBattleActivePhase phase) {
		super(phase);
	}

	private Component getEndingMessage() {
		if (this.phase.getPlayers().size() == 1) {
			Player winner = this.phase.getPlayers().iterator().next().getPlayer();
			return Component.translatable("text.microbattle.win", winner.getDisplayName()).withStyle(ChatFormatting.GOLD);
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
