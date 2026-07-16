package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.stimuli.event.EventResult;

public class SheepKit extends Kit {
	private static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
	private static final BlockState WOOL_COAT = Blocks.COBWEB.defaultBlockState();
	private static final int WOOL_COAT_REQUIRED_GRASS = 25;

	private int grassEaten = 0;

	public SheepKit(PlayerEntry entry) {
		super(KitTypes.SHEEP, entry);
	}

	@Override
	protected int getBaseColor() {
		return 0xCBA272;
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.WHITE.getFireworkColor();
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Eat grass to replenish your wool coat",
			"Wrap your wool coat around enemies by attacking them",
		};
	}

	private void updateExperienceBarForWoolCoat() {
		this.setExperienceBar(grassEaten / (float) WOOL_COAT_REQUIRED_GRASS);
	}

	@Override
	public InteractionResult onUseBlock(InteractionHand hand, BlockHitResult hitResult) {
		if (hitResult.getDirection() != Direction.DOWN) {
			BlockPos pos = hitResult.getBlockPos();
			Level world = this.player.level();
			BlockState state = world.getBlockState(pos);

			boolean grassBlock = state.is(Blocks.GRASS_BLOCK);
			if (state.is(Blocks.SHORT_GRASS) || grassBlock) {
				if (grassBlock) {
					world.levelEvent(2001, pos, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
					world.setBlock(pos, DIRT, 2);
				} else {
					world.destroyBlock(pos, false, this.player);
				}

				this.grassEaten += 1;
				this.updateExperienceBarForWoolCoat();

				if (this.grassEaten == WOOL_COAT_REQUIRED_GRASS) {
					world.playSound(null, this.player, SoundEvents.SHEEP_AMBIENT, SoundSource.PLAYERS, 1, 1);
				}

				return InteractionResult.FAIL;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public EventResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		if (this.grassEaten < WOOL_COAT_REQUIRED_GRASS) return EventResult.PASS;

		this.grassEaten = 0;
		this.updateExperienceBarForWoolCoat();

		Level world = this.player.level();
		BlockPos pos = target.getPlayer().blockPosition();

		world.setBlockAndUpdate(pos, WOOL_COAT);
		world.setBlockAndUpdate(pos.above(), WOOL_COAT);

		world.playSound(null, this.player, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1, 1);

		return EventResult.PASS;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SHEEP_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SHEEP_HURT;
	}
}
