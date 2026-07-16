package io.github.haykam821.microbattle.game.kit;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.PlayerEntry;
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
		if (!state.is(Main.RESPAWN_BEACONS)) return EventResult.PASS;
		return this.phase.placeBeacon(entry, this, pos) ? EventResult.ALLOW : EventResult.DENY;
	}

	@Override
	public EventResult onBreakBlock(BlockPos pos) {
		// Prevent breaking own beacon
		if (this.isRespawnPos(pos, false)) {
			this.player.sendSystemMessage(Component.translatable("text.microbattle.cannot_break_own_beacon").withStyle(ChatFormatting.RED), false);
			return EventResult.DENY;
		}

		return EventResult.PASS;
	}

	private Vec3 getRespawnAroundPos(BlockPos beaconPos) {
		Optional<Vec3> spawnOptional = RespawnAnchorBlock.findStandUpPosition(EntityTypes.PLAYER, this.phase.getWorld(), beaconPos);
		if (spawnOptional.isPresent()) {
			Vec3 spawn = spawnOptional.get();
			if (spawn.y() <= 255) {
				return spawn;
			}
		}
		return new Vec3(beaconPos.getX() + 0.5, beaconPos.getY() + 1, beaconPos.getZ() + 0.5);
	}

	@Override
	public EventResult attemptRespawn() {
		if (this.respawnPos == null || this.beaconBroken) {
			return EventResult.DENY;
		}

		ServerLevel world = this.phase.getWorld();
		BlockState respawnState = world.getBlockState(this.respawnPos);
		if (!respawnState.is(Main.RESPAWN_BEACONS)) {
			return EventResult.DENY;
		}

		// Reset state
		ServerPlayer player = this.entry.getPlayer();

		player.setHealth(player.getMaxHealth());
		player.getFoodData().setFoodLevel(20);
		player.setAirSupply(player.getMaxAirSupply());

		player.clearFire();
		player.getCombatTracker().recheckStatus();
		player.fallDistance = 0;

		// Teleport and spawn
		Vec3 spawn = this.getRespawnAroundPos(respawnPos);
		player.teleportTo(world, spawn.x(), spawn.y(), spawn.z(), Set.of(), 0, 0, true);
		this.entry.getKit().reinitialize();

		return EventResult.ALLOW;
	}
}
