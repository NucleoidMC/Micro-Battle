package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.event.EventResult;

public class SheepKit extends Kit {
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState WOOL_COAT = Blocks.COBWEB.getDefaultState();
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
	public ActionResult onUseBlock(Hand hand, BlockHitResult hitResult) {
		if (hitResult.getSide() != Direction.DOWN) {
			BlockPos pos = hitResult.getBlockPos();
			World world = this.player.getEntityWorld();
			BlockState state = world.getBlockState(pos);

			boolean grassBlock = state.isOf(Blocks.GRASS_BLOCK);
			if (state.isOf(Blocks.SHORT_GRASS) || grassBlock) {
				if (grassBlock) {
					world.syncWorldEvent(2001, pos, Block.getRawIdFromState(Blocks.GRASS_BLOCK.getDefaultState()));
					world.setBlockState(pos, DIRT, 2);
				} else {
					world.breakBlock(pos, false, this.player);
				}

				this.grassEaten += 1;
				this.updateExperienceBarForWoolCoat();

				if (this.grassEaten == WOOL_COAT_REQUIRED_GRASS) {
					world.playSoundFromEntity(null, this.player, SoundEvents.ENTITY_SHEEP_AMBIENT, SoundCategory.PLAYERS, 1, 1);
				}

				return ActionResult.FAIL;
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public EventResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		if (this.grassEaten < WOOL_COAT_REQUIRED_GRASS) return EventResult.PASS;

		this.grassEaten = 0;
		this.updateExperienceBarForWoolCoat();

		World world = this.player.getEntityWorld();
		BlockPos pos = target.getPlayer().getBlockPos();

		world.setBlockState(pos, WOOL_COAT);
		world.setBlockState(pos.up(), WOOL_COAT);

		world.playSoundFromEntity(null, this.player, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1, 1);

		return EventResult.PASS;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHEEP_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SHEEP_HURT;
	}
}
