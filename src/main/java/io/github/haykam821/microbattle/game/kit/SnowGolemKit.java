package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class SnowGolemKit extends Kit {
	private static final BlockState SNOW = Blocks.SNOW.getDefaultState();
	private static final BlockState FROSTED_ICE = Blocks.FROSTED_ICE.getDefaultState();
	
	public SnowGolemKit(PlayerEntry entry) {
		super(KitTypes.SNOW_GOLEM, entry);
		this.addRestockEntry(new RestockEntry.Builder(Items.SNOWBALL, 10).maxCount(16).build(entry.getPlayer().getRegistryManager()));
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
		
		if (!this.player.isSneaking()) {
			this.tickTrail();
		}
	}

	private void tickTrail() {
		ServerWorld world = this.player.getServerWorld();

		BlockPos.Mutable pos = new BlockPos.Mutable(0, Math.floor(this.player.getY()), 0);
		for (int corner = 0; corner < 4; corner++) {
			pos.setX((int) (this.player.getX() + (corner % 2 * 2 - 1) * 0.25));
			pos.setZ((int) (this.player.getZ() + (corner / 2 % 2 * 2 - 1) * 0.25));

			BlockPos downPos = pos.down();

			if (SnowGolemKit.canPlaceSnowAt(world, pos, downPos)) {
				world.setBlockState(pos, SNOW);
			} else if (SnowGolemKit.isStillWater(world, downPos)) {
				world.setBlockState(downPos, FROSTED_ICE);
			}
		}
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
	}

	private static boolean canPlaceSnowAt(ServerWorld world, BlockPos pos, BlockPos downPos) {
		return (
			world.getBlockState(pos).isAir()
			&& SNOW.canPlaceAt(world, pos)
			&& !world.getBlockState(downPos).isIn(BlockTags.ICE)
		);
	}

	private static boolean isStillWater(ServerWorld world, BlockPos pos) {
		FluidState state = world.getFluidState(pos);
		return state.isStill() && state.isIn(FluidTags.WATER);
	}
}
