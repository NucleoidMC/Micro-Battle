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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
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
			throw new GameOpenException(new TranslatableText("text.microbattle.not_enough_kits"));
		}

		MicroBattleMapBuilder mapBuilder = new MicroBattleMapBuilder(context.config());
		MicroBattleMap map = mapBuilder.create();

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
			activity.listen(GamePlayerEvents.OFFER, phase::offerPlayer);
			activity.listen(PlayerDeathEvent.EVENT, phase::onPlayerDeath);
			activity.listen(GamePlayerEvents.LEAVE, phase::onPlayerLeave);
			activity.listen(OpenKitSelectionListener.EVENT, phase::openKitSelection);
			activity.listen(GameActivityEvents.REQUEST_START, phase::requestStart);
		});
	}

	public ActionResult openKitSelection(World world, ServerPlayerEntity user, Hand hand) {
		KitSelectionUi.build(this.kitSelection, user).open();
		return ActionResult.SUCCESS;
	}

	private GameResult requestStart() {
		MicroBattleActivePhase.open(this.gameSpace, this.world, this.map, this.teamSelection, this.kitSelection, this.config);
		return GameResult.ok();
	}

	/**
	 * Gives the kit selector item to a player if necessary.
	 */
	private void giveKitSelector(ServerPlayerEntity player) {
		if (!this.kitSelection.isKitSelectorNecessary()) {
			return;
		}

		player.getInventory().setStack(8, new ItemStack(Main.KIT_SELECTOR));

		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.getInventory());
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.world, MicroBattleActivePhase.getSpawnPos(this.world, this.map, offer.player())).and(() -> {
			offer.player().changeGameMode(GameMode.ADVENTURE);
			this.giveKitSelector(offer.player());
		});
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		MicroBattleActivePhase.spawn(this.world, this.map, player);
		return ActionResult.FAIL;
	}

	private void onPlayerLeave(ServerPlayerEntity player) {
		this.kitSelection.deselect(player);
	}
}