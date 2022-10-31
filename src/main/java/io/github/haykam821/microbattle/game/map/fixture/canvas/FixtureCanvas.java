package io.github.haykam821.microbattle.game.map.fixture.canvas;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public interface FixtureCanvas {
	public void setBlockState(int x, int y, int z, BlockState state);
	public void setBlockState(int x, int y, int z, BlockStateProvider provider, Random random);
}
