package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Optional;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RespawnerKit extends PlayerKit {
	private BlockPos respawnPos;

	public RespawnerKit(PlayerEntry entry) {
		super(KitTypes.RESPAWNER, entry);
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Place down your beacon in a safe place",
			"You will respawn from this beacon if you die",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You won't respawn if your beacon is broken",
		};
	}

	@Override
	protected ItemStack getChestplateStack() {
		return this.createArmorStack(Items.CHAINMAIL_CHESTPLATE, "chestplate", false);
	}

	@Override
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		super.appendCustomInitialStacks(stacks);
		if (this.respawnPos == null) {
			stacks.add(new ItemStack(Items.BEACON));
		}
	}

	public boolean hasRespawnPos() {
		return this.respawnPos != null;
	}

	@Override
	public boolean isRespawnPos(BlockPos pos) {
		return pos.equals(this.respawnPos);
	}

	public void setRespawnPos(BlockPos respawnPos) {
		this.respawnPos = respawnPos;
	}

	@Override
	public ActionResult afterBlockPlace(BlockPos pos, ItemStack stack, BlockState state) {
		if (!state.isIn(Main.RESPAWN_BEACONS)) return ActionResult.PASS;
		return this.phase.placeBeacon(entry, this, pos) ? ActionResult.SUCCESS : ActionResult.FAIL;
	}

	@Override
	public ActionResult onBreakBlock(BlockPos pos) {
		// Prevent breaking own beacon
		if (this.isRespawnPos(pos)) {
			this.player.sendMessage(Text.translatable("text.microbattle.cannot_break_own_beacon").formatted(Formatting.RED), false);
			return ActionResult.FAIL;
		}

		return ActionResult.PASS;
	}

	private Vec3d getRespawnAroundPos(BlockPos beaconPos) {
		Optional<Vec3d> spawnOptional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, this.phase.getWorld(), beaconPos);
		if (spawnOptional.isPresent()) {
			Vec3d spawn = spawnOptional.get();
			if (spawn.getY() <= 255) {
				return spawn;
			}
		}
		return new Vec3d(beaconPos.getX() + 0.5, beaconPos.getY(), beaconPos.getZ() + 0.5);
	}

	@Override
	public ActionResult attemptRespawn() {
		if (this.respawnPos == null) {
			return ActionResult.FAIL;
		}

		ServerWorld world = this.phase.getWorld();
		BlockState respawnState = world.getBlockState(this.respawnPos);
		if (!respawnState.isIn(Main.RESPAWN_BEACONS)) {
			return ActionResult.FAIL;
		}

		// Reset state
		entry.getPlayer().setHealth(entry.getPlayer().getMaxHealth());
		entry.getPlayer().getHungerManager().setFoodLevel(20);
		entry.getPlayer().extinguish();
		entry.getPlayer().getDamageTracker().update();
		entry.getPlayer().fallDistance = 0;

		// Teleport and spawn
		Vec3d spawn = this.getRespawnAroundPos(respawnPos);
		entry.getPlayer().teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);;
		entry.getKit().reinitialize();

		return ActionResult.SUCCESS;
	}
}
