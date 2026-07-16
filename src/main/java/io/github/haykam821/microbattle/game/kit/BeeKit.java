package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.stimuli.event.EventResult;

public class BeeKit extends Kit {
	public BeeKit(PlayerEntry entry) {
		super(KitTypes.BEE, entry);
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"Water is harmful to you",
		};
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.YELLOW.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.BLACK.getFireworkColor();
	}

	@Override
	public boolean isDamagedByWater() {
		return true;
	}

	private void placeFlower(ServerLevel world, BlockPos pos) {
		BlockState flower = BeeKit.getFlower(world.registryAccess(), world.getRandom());
		if (flower != null && world.isEmptyBlock(pos) && flower.canSurvive(world, pos)) {
			world.setBlockAndUpdate(pos, flower);
		}
	}
	
	@Override
	public EventResult onKilledPlayer(PlayerEntry entry, DamageSource source) {
		if (entry.getPlayer().onGround()) {
			this.placeFlower(entry.getPlayer().level(), entry.getPlayer().blockPosition());
		}
		return EventResult.PASS;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.BEE_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BEE_HURT;
	}

	private static BlockState getFlower(HolderLookup.Provider registries, RandomSource random) {
		return registries
			.lookup(Registries.BLOCK)
			.flatMap(blocks -> blocks.get(BlockTags.FLOWERS))
			.flatMap(flowers -> flowers.getRandomElement(random))
			.map(Holder::value)
			.map(Block::defaultBlockState)
			.orElse(null);
	}
}
