package io.github.haykam821.microbattle.game.win;

import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamKey;

public class TeamWinManager extends WinManager {
	private final Object2IntOpenHashMap<GameTeamKey> playerCounts = new Object2IntOpenHashMap<>();

	public TeamWinManager(MicroBattleActivePhase phase) {
		super(phase);
		this.playerCounts.defaultReturnValue(0);
	}

	private Text getWinningTeamMessage(GameTeamKey teamKey) {
		Text teamName = this.phase.getTeamConfig(teamKey).name();
		return Text.translatable("text.microbattle.team_win", teamName).formatted(Formatting.GOLD);
	}

	@Override
	public boolean checkForWinner() {
		this.playerCounts.clear();
		for (PlayerEntry entry : this.phase.getPlayers()) {
			if (entry.getTeamKey() != null) {
				this.playerCounts.addTo(entry.getTeamKey(), 1);
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

		GameTeamKey winningTeam = null;
		for (Object2IntMap.Entry<GameTeamKey> entry : this.playerCounts.object2IntEntrySet()) {
			if (entry.getIntValue() > 0) {
				if (winningTeam != null) return false;
				winningTeam = entry.getKey();
			}
		}

		this.phase.getGameSpace().getPlayers().sendMessage(this.getWinningTeamMessage(winningTeam));
		return true;
	}
}
