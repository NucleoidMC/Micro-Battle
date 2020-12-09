package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.player.GameTeam;

public class TeamWinManager extends WinManager {
	private final Object2IntOpenHashMap<GameTeam> playerCounts = new Object2IntOpenHashMap<>();

	public TeamWinManager(MicroBattleActivePhase phase) {
		super(phase);
		this.playerCounts.defaultReturnValue(0);
	}

	private Text getWinningTeamMessage(GameTeam team) {
		Text teamName = new LiteralText(team.getDisplay()).formatted(team.getFormatting());
		return new TranslatableText("text.microbattle.team_win", teamName).formatted(Formatting.GOLD);
	}

	@Override
	public boolean checkForWinner() {
		this.playerCounts.clear();
		for (PlayerEntry entry : this.phase.getPlayers()) {
			if (entry.getTeam() != null) {
				this.playerCounts.addTo(entry.getTeam(), 1);
			}
		}

		// No teams means no players
		if (this.playerCounts.isEmpty()) {
			this.phase.getGameSpace().getPlayers().sendMessage(this.getNoWinnersMessage());
			return true;
		}

		if (this.phase.isSingleplayer()) {
			return false;
		}

		GameTeam winningTeam = null;
		for (Object2IntMap.Entry<GameTeam> entry : this.playerCounts.object2IntEntrySet()) {
			if (entry.getIntValue() > 0) {
				if (winningTeam != null) return false;
				winningTeam = entry.getKey();
			}
		}

		this.phase.getGameSpace().getPlayers().sendMessage(this.getWinningTeamMessage(winningTeam));
		return true;
	}
}
