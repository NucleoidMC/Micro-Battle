package io.github.haykam821.microbattle.game.map.fixture.canvas;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import xyz.nucleoid.map_templates.MapTemplate;

public class TemplateFixtureCanvas implements FixtureCanvas {
	private final MapTemplate template;
	private final BlockPos.MutableBlockPos pointer = new BlockPos.MutableBlockPos();

	private BlockPos start;

	public TemplateFixtureCanvas(MapTemplate template) {
		this.template = template;
	}

	public void setStart(BlockPos start) {
		this.start = start;
	}

	private void setPointer(int x, int y, int z) {
		this.pointer.set(this.start.getX() + x, this.start.getY() + y, this.start.getZ() + z);
	}

	@Override
	public void setBlockState(int x, int y, int z, BlockState state) {
		this.setPointer(x, y, z);
		this.template.setBlockState(this.pointer, state);
	}

	@Override
	public void setBlockState(int x, int y, int z, BlockStateProvider provider, RandomSource random) {
		this.setPointer(x, y, z);
		BlockState state = provider.getState(null, random, this.pointer);
		this.template.setBlockState(this.pointer, state);
	}
}
