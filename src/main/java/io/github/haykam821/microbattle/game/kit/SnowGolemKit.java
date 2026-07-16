package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SnowGolemKit extends Kit {
	private static final BlockState SNOW = Blocks.SNOW.defaultBlockState();
	private static final BlockState FROSTED_ICE = Blocks.FROSTED_ICE.defaultBlockState();
	
	public SnowGolemKit(PlayerEntry entry) {
		super(KitTypes.SNOW_GOLEM, entry);
		this.addRestockEntry(new RestockEntry.Builder(Items.SNOWBALL, 10).maxCount(16).build(entry.getPlayer().registryAccess()));
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You can quickly throw snowballs",
			"You leave behind a trail of snow and ice",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"Water is harmful to you",
		};
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.WHITE.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.WHITE.getFireworkColor();
	}

	@Override
	protected ItemStack getHelmetStack() {
		return this.createArmorStack(Items.CARVED_PUMPKIN, "helmet", true);
	}

	@Override
	public boolean isDamagedByWater() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		
		if (!this.player.isShiftKeyDown()) {
			this.tickTrail();
		}
	}

	private void tickTrail() {
		ServerLevel world = this.player.level();

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, Math.floor(this.player.getY()), 0);
		for (int corner = 0; corner < 4; corner++) {
			pos.setX((int) (this.player.getX() + (corner % 2 * 2 - 1) * 0.25));
			pos.setZ((int) (this.player.getZ() + (corner / 2 % 2 * 2 - 1) * 0.25));

			BlockPos downPos = pos.below();

			if (SnowGolemKit.canPlaceSnowAt(world, pos, downPos)) {
				world.setBlockAndUpdate(pos, SNOW);
			} else if (SnowGolemKit.isStillWater(world, downPos)) {
				world.setBlockAndUpdate(downPos, FROSTED_ICE);
			}
		}
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SNOW_GOLEM_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SNOW_GOLEM_HURT;
	}

	private static boolean canPlaceSnowAt(ServerLevel world, BlockPos pos, BlockPos downPos) {
		return (
			world.getBlockState(pos).isAir()
			&& SNOW.canSurvive(world, pos)
			&& !world.getBlockState(downPos).is(BlockTags.ICE)
		);
	}

	private static boolean isStillWater(ServerLevel world, BlockPos pos) {
		FluidState state = world.getFluidState(pos);
		return state.isSource() && state.is(FluidTags.WATER);
	}
}
