package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.stimuli.event.EventResult;

public class RespawnerKit extends PlayerKit {
	private BlockPos respawnPos;
	private boolean beaconBroken = false;

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
		if (this.respawnPos == null && !this.beaconBroken) {
			stacks.add(new ItemStack(Items.BEACON));
		}
	}

	public boolean hasRespawnPos() {
		return this.respawnPos != null;
	}

	@Override
	public boolean isRespawnPos(BlockPos pos, boolean clear) {
		if (pos.equals(this.respawnPos) && !this.beaconBroken) {
			if (clear) {
				this.respawnPos = null;
				this.beaconBroken = true;
			}

			return true;
		}

		return false;
	}

	public void setRespawnPos(BlockPos respawnPos) {
		this.respawnPos = respawnPos;
	}

	@Override
	public EventResult afterBlockPlace(BlockPos pos, ItemStack stack, BlockState state) {
		if (!state.isIn(Main.RESPAWN_BEACONS)) return EventResult.PASS;
		return this.phase.placeBeacon(entry, this, pos) ? EventResult.ALLOW : EventResult.DENY;
	}

	@Override
	public EventResult onBreakBlock(BlockPos pos) {
		// Prevent breaking own beacon
		if (this.isRespawnPos(pos, false)) {
			this.player.sendMessage(Text.translatable("text.microbattle.cannot_break_own_beacon").formatted(Formatting.RED), false);
			return EventResult.DENY;
		}

		return EventResult.PASS;
	}

	private Vec3d getRespawnAroundPos(BlockPos beaconPos) {
		Optional<Vec3d> spawnOptional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, this.phase.getWorld(), beaconPos);
		if (spawnOptional.isPresent()) {
			Vec3d spawn = spawnOptional.get();
			if (spawn.getY() <= 255) {
				return spawn;
			}
		}
		return new Vec3d(beaconPos.getX() + 0.5, beaconPos.getY() + 1, beaconPos.getZ() + 0.5);
	}

	@Override
	public EventResult attemptRespawn() {
		if (this.respawnPos == null || this.beaconBroken) {
			return EventResult.DENY;
		}

		ServerWorld world = this.phase.getWorld();
		BlockState respawnState = world.getBlockState(this.respawnPos);
		if (!respawnState.isIn(Main.RESPAWN_BEACONS)) {
			return EventResult.DENY;
		}

		// Reset state
		ServerPlayerEntity player = this.entry.getPlayer();

		player.setHealth(player.getMaxHealth());
		player.getHungerManager().setFoodLevel(20);
		player.setAir(player.getMaxAir());

		player.extinguish();
		player.getDamageTracker().update();
		player.fallDistance = 0;

		// Teleport and spawn
		Vec3d spawn = this.getRespawnAroundPos(respawnPos);
		player.teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), Set.of(), 0, 0, true);
		this.entry.getKit().reinitialize();

		return EventResult.ALLOW;
	}
}
