package io.github.haykam821.microbattle.game.kit;

import java.util.Random;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

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
		BlockState flower = BeeKit.getFlower(world.getRandom());
		if (flower != null && world.isAir(pos) && flower.canPlaceAt(world, pos)) {
			world.setBlockState(pos, flower);
		}
	}
	
	@Override
	public ActionResult onKilledPlayer(PlayerEntry entry, DamageSource source) {
		if (entry.getPlayer().isOnGround()) {
			this.placeFlower(entry.getPlayer().getWorld(), entry.getPlayer().getBlockPos());
		}
		return ActionResult.PASS;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	private static BlockState getFlower(Random random) {
		return Registry.BLOCK.getEntryList(BlockTags.FLOWERS)
			.flatMap(flowers -> flowers.getRandom(random))
			.map(RegistryEntry::value)
			.map(Block::getDefaultState)
			.orElse(null);
	}
}
