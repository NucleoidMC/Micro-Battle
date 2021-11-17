package io.github.haykam821.microbattle.game.map;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.MapTemplate;

public class Building {
	private static final DataPool<BlockState> STATES = DataPool.<BlockState>builder()
		.add(Blocks.STONE_BRICKS.getDefaultState(), 20)
		.add(Blocks.COBBLESTONE.getDefaultState(), 5)
		.add(Blocks.BRICKS.getDefaultState(), 5)
		.add(Blocks.PRISMARINE_BRICKS.getDefaultState(), 1)
		.add(Blocks.END_STONE_BRICKS.getDefaultState(), 1)
		.add(Blocks.NETHER_BRICKS.getDefaultState(), 1)
		.add(Blocks.RED_NETHER_BRICKS.getDefaultState(), 1)
		.add(Blocks.QUARTZ_BRICKS.getDefaultState(), 1)
		.add(Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState(), 1)
		.add(Blocks.DEEPSLATE_BRICKS.getDefaultState(), 1)
		.build();

	private final int width;
	private final int height;
	private final int depth;
	private final BlockState state;

	private Building(int width, int height, int depth, BlockState state) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.state = state;
	}
	
	public void generate(MapTemplate template, int startX, int startY, int startZ) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int x = 0; x < this.width; x++) {
			for (int z = 0; z < this.depth; z++) {
				boolean border = x == 0 || x == this.width - 1 || z == 0 || z == this.depth - 1;
				for (int y = 0; y < this.height; y++) {
					if (y >= this.height - 2 && !border) continue;
					if (y == this.height - 1 && (x + z) % 2 == 1) continue;

					pos.set(startX + x, startY + y, startZ + z);
					template.setBlockState(pos, this.state);
				}
			}
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getDepth() {
		return this.depth;
	}

	@Override
	public String toString() {
		return "Building{width=" + this.width + ", height= " + this.height + ", depth=" + this.depth + ", state=" + this.state + "}";
	}

	public static Building randomizeHeight(Random random, int size) {
		BlockState state = STATES.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
		return new Building(size, random.nextInt(4) + 6, size, state);
	}
}
