package io.github.haykam821.microbattle.game.phase;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.event.OpenKitSelectionListener;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectionManager;
import io.github.haykam821.microbattle.game.kit.selection.KitSelectionUi;
import io.github.haykam821.microbattle.game.map.MicroBattleMap;
import io.github.haykam821.microbattle.game.map.MicroBattleMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class MicroBattleWaitingPhase {
	private final GameSpace gameSpace;
	private final MicroBattleMap map;
	private final TeamSelectionLobby teamSelection;
	private final MicroBattleConfig config;
	private final KitSelectionManager kitSelection;

	public MicroBattleWaitingPhase(GameSpace gameSpace, MicroBattleMap map, TeamSelectionLobby teamSelection, MicroBattleConfig config) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.teamSelection = teamSelection;
		this.config = config;
		this.kitSelection = new KitSelectionManager(this.config.getKits());
	}

	public static GameOpenProcedure open(GameOpenContext<MicroBattleConfig> context) {
		MicroBattleConfig config = context.getConfig();
		if (context.getConfig().getKits().isEmpty()) {
			throw new GameOpenException(new TranslatableText("text.microbattle.not_enough_kits"));
		}

		MicroBattleMapBuilder mapBuilder = new MicroBattleMapBuilder(context.getConfig());
		MicroBattleMap map = mapBuilder.create();

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
			.setGenerator(map.createGenerator(context.getServer()))
			.setDefaultGameMode(GameMode.ADVENTURE);

		return context.createOpenProcedure(worldConfig, game -> {
			TeamSelectionLobby teamSelection = config.getTeams().isPresent() ? TeamSelectionLobby.applyTo(game, config.getTeams().get()) : null;

			MicroBattleWaitingPhase phase = new MicroBattleWaitingPhase(game.getSpace(), map, teamSelection, config);
			GameWaitingLobby.applyTo(game, config.getPlayerConfig());
			
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
			game.setRule(GameRule.BREAK_BLOCKS, RuleResult.DENY);
			game.setRule(GameRule.CRAFTING, RuleResult.DENY);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
			game.setRule(Main.FLUID_FLOW, RuleResult.DENY);
			game.setRule(GameRule.HUNGER, RuleResult.DENY);
			game.setRule(GameRule.INTERACTION, RuleResult.DENY);
			game.setRule(GameRule.PLACE_BLOCKS, RuleResult.DENY);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);

			// Listeners
			game.on(PlayerAddListener.EVENT, phase::addPlayer);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
			game.on(PlayerRemoveListener.EVENT, phase::onRemovePlayer);
			game.on(OfferPlayerListener.EVENT, phase::offerPlayer);
			game.on(OpenKitSelectionListener.EVENT, phase::openKitSelection);
			game.on(RequestStartListener.EVENT, phase::requestStart);
		});
	}

	private boolean isFull() {
		return this.gameSpace.getPlayerCount() >= this.config.getPlayerConfig().getMaxPlayers();
	}

	private JoinResult offerPlayer(ServerPlayerEntity player) {
		return this.isFull() ? JoinResult.gameFull() : JoinResult.ok();
	}

	public ActionResult openKitSelection(World world, ServerPlayerEntity user, Hand hand) {
		user.openHandledScreen(KitSelectionUi.build(this.kitSelection, user));
		return ActionResult.SUCCESS;
	}

	private StartResult requestStart() {
		PlayerConfig playerConfig = this.config.getPlayerConfig();
		if (this.gameSpace.getPlayerCount() < playerConfig.getMinPlayers()) {
			return StartResult.NOT_ENOUGH_PLAYERS;
		}

		MicroBattleActivePhase.open(this.gameSpace, this.map, this.teamSelection, this.kitSelection, this.config);
		return StartResult.OK;
	}

	/**
	 * Gives the kit selector item to a player if necessary.
	 */
	private void giveKitSelector(ServerPlayerEntity player) {
		if (!this.kitSelection.isKitSelectorNecessary()) {
			return;
		}

		player.inventory.setStack(8, new ItemStack(Main.KIT_SELECTOR));

		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}

	private void addPlayer(ServerPlayerEntity player) {
		this.giveKitSelector(player);
		MicroBattleActivePhase.spawn(this.gameSpace.getWorld(), this.map, player);
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		MicroBattleActivePhase.spawn(this.gameSpace.getWorld(), this.map, player);
		return ActionResult.FAIL;
	}

	private void onRemovePlayer(ServerPlayerEntity player) {
		this.kitSelection.deselect(player);
	}
}