package io.github.haykam821.microbattle.game.phase;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectionManager;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectionWaitingLobbyUiElement;
import io.github.haykam821.microbattle.game.map.MicroBattleMap;
import io.github.haykam821.microbattle.game.map.MicroBattleMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenException;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.api.game.common.ui.WaitingLobbyUiLayout;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.event.GameWaitingLobbyEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class MicroBattleWaitingPhase {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final MicroBattleMap map;
	private final TeamSelectionLobby teamSelection;
	private final MicroBattleConfig config;
	private final KitSelectionManager kitSelection;

	public MicroBattleWaitingPhase(GameSpace gameSpace, ServerWorld world, MicroBattleMap map, TeamSelectionLobby teamSelection, MicroBattleConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.teamSelection = teamSelection;
		this.config = config;
		this.kitSelection = new KitSelectionManager(this.config.getKits());
	}

	public static GameOpenProcedure open(GameOpenContext<MicroBattleConfig> context) {
		MicroBattleConfig config = context.config();
		if (context.config().getKits().isEmpty()) {
			throw new GameOpenException(Text.translatable("text.microbattle.not_enough_kits"));
		}

		MicroBattleMapBuilder mapBuilder = new MicroBattleMapBuilder(context.config());
		MicroBattleMap map = mapBuilder.create(context.server());

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
			.setGenerator(map.createGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			TeamSelectionLobby teamSelection = config.getTeams().isPresent() ? TeamSelectionLobby.addTo(activity, config.getTeams().get()) : null;

			MicroBattleWaitingPhase phase = new MicroBattleWaitingPhase(activity.getGameSpace(), world, map, teamSelection, config);
			GameWaitingLobby.addTo(activity, config.getPlayerConfig());
			
			activity.deny(GameRuleType.BLOCK_DROPS);
			activity.deny(GameRuleType.BREAK_BLOCKS);
			activity.deny(GameRuleType.CRAFTING);
			activity.deny(GameRuleType.FALL_DAMAGE);
			activity.deny(GameRuleType.FLUID_FLOW);
			activity.deny(GameRuleType.HUNGER);
			activity.allow(GameRuleType.INTERACTION);
			activity.deny(GameRuleType.USE_BLOCKS);
			activity.deny(GameRuleType.USE_ENTITIES);
			activity.deny(GameRuleType.MODIFY_ARMOR);
			activity.deny(GameRuleType.PLACE_BLOCKS);
			activity.deny(GameRuleType.PORTALS);
			activity.deny(GameRuleType.PVP);
			activity.deny(GameRuleType.THROW_ITEMS);

			// Listeners
			activity.listen(GamePlayerEvents.ACCEPT, phase::onAcceptPlayers);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			activity.listen(PlayerDeathEvent.EVENT, phase::onPlayerDeath);
			activity.listen(GamePlayerEvents.LEAVE, phase::onPlayerLeave);
			activity.listen(GameWaitingLobbyEvents.BUILD_UI_LAYOUT, phase::buildUiLayout);
			activity.listen(GameActivityEvents.REQUEST_START, phase::requestStart);
		});
	}

	public void buildUiLayout(WaitingLobbyUiLayout layout, ServerPlayerEntity player) {
		if (this.gameSpace.getPlayers().participants().contains(player) && this.kitSelection.isKitSelectorNecessary()) {
			layout.addTrailing(new KitSelectionWaitingLobbyUiElement(this.kitSelection));
		}
	}

	private GameResult requestStart() {
		MicroBattleActivePhase.open(this.gameSpace, this.world, this.map, this.teamSelection, this.kitSelection, this.config);
		return GameResult.ok();
	}

	private JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return acceptor.teleport(this.world, MicroBattleActivePhase.getSpawnPos(this.world, this.map)).thenRunForEach(player -> {
			player.changeGameMode(GameMode.ADVENTURE);
		});
	}

	private EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		MicroBattleActivePhase.spawn(this.world, this.map, player);
		return EventResult.DENY;
	}

	private void onPlayerLeave(ServerPlayerEntity player) {
		this.kitSelection.deselect(player);
	}
}