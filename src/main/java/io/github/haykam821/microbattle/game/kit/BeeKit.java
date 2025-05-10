package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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

	private void placeFlower(ServerWorld world, BlockPos pos) {
		BlockState flower = BeeKit.getFlower(world.getRegistryManager(), world.getRandom());
		if (flower != null && world.isAir(pos) && flower.canPlaceAt(world, pos)) {
			world.setBlockState(pos, flower);
		}
	}
	
	@Override
	public EventResult onKilledPlayer(PlayerEntry entry, DamageSource source) {
		if (entry.getPlayer().isOnGround()) {
			this.placeFlower(entry.getPlayer().getServerWorld(), entry.getPlayer().getBlockPos());
		}
		return EventResult.PASS;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	private static BlockState getFlower(RegistryWrapper.WrapperLookup registries, Random random) {
		return registries
			.getOptional(RegistryKeys.BLOCK)
			.flatMap(blocks -> blocks.getOptional(BlockTags.FLOWERS))
			.flatMap(flowers -> flowers.getRandom(random))
			.map(RegistryEntry::value)
			.map(Block::getDefaultState)
			.orElse(null);
	}
}
