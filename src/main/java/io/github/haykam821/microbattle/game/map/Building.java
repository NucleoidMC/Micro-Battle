package io.github.haykam821.microbattle.game.map;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

public class Building {
	private static final WeightedList<BlockState> STATES = new WeightedList<>();

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
		return new Building(size, random.nextInt(4) + 6, size, STATES.pickRandom(random));
	}

	public static Building randomize(Random random) {
		int size = random.nextInt(2) + 3;
		if (size % 2 == 0) size += 1;

		return randomizeHeight(random, size);
	}

	static {
		STATES.add(Blocks.STONE_BRICKS.getDefaultState(), 20);
		STATES.add(Blocks.COBBLESTONE.getDefaultState(), 5);
		STATES.add(Blocks.BRICKS.getDefaultState(), 5);
		STATES.add(Blocks.PRISMARINE_BRICKS.getDefaultState(), 1);
		STATES.add(Blocks.END_STONE_BRICKS.getDefaultState(), 1);
		STATES.add(Blocks.NETHER_BRICKS.getDefaultState(), 1);
		STATES.add(Blocks.RED_NETHER_BRICKS.getDefaultState(), 1);
		STATES.add(Blocks.QUARTZ_BRICKS.getDefaultState(), 1);
		STATES.add(Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState(), 1);
	}
}
