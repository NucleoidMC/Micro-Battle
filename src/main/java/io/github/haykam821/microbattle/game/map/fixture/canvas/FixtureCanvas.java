package io.github.haykam821.microbattle.game.map.fixture.canvas;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public interface FixtureCanvas {
	public void setBlockState(int x, int y, int z, BlockState state);
	public void setBlockState(int x, int y, int z, BlockStateProvider provider, RandomSource random);
}
