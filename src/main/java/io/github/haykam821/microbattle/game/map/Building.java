package io.github.haykam821.microbattle.game.map;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.random.AbstractRandom;
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

	private static final BlockState VINE = Blocks.VINE.getDefaultState();

	private final int width;
	private final int height;
	private final int depth;

	private final double vineDensity;
	private final BlockState state;

	private Building(int width, int height, int depth, double vineDensity, BlockState state) {
		this.width = width;
		this.height = height;
		this.depth = depth;

		this.vineDensity = vineDensity;
		this.state = state;
	}
	
	public void generate(MapTemplate template, int startX, int startY, int startZ) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		Random random = new Random();

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

		if (this.vineDensity > 0) {
			this.generateVineSide(template, pos, random, Direction.NORTH, startX, startY, startZ + this.depth, startX + this.width - 1, startY + this.height - 2, startZ + this.depth);
			this.generateVineSide(template, pos, random, Direction.EAST, startX - 1, startY, startZ, startX - 1, startY + this.height - 2, startZ + this.depth - 1);
			this.generateVineSide(template, pos, random, Direction.SOUTH, startX, startY, startZ - 1, startX + this.width - 1, startY + this.height - 2, startZ - 1);
			this.generateVineSide(template, pos, random, Direction.WEST, startX + this.width, startY, startZ, startX + this.width, startY + this.height - 2, startZ + this.depth - 1);
		}
	}

	private void generateVineSide(MapTemplate template, BlockPos.Mutable pos, Random random, Direction facing, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		BlockState vine = VINE.with(VineBlock.getFacingProperty(facing), true);

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (this.vineDensity == 1 || random.nextDouble() < this.vineDensity) {
						pos.set(x, y, z);
						template.setBlockState(pos, vine);
					}
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

	public static Building randomize(Random random, int size) {
		double vineDensity = random.nextInt(4) == 0 ? random.nextDouble(0.3, 0.9) : 0;

		BlockState state = STATES.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
		return new Building(size, random.nextInt(4) + 6, size, vineDensity, state);
	}

	public static Building randomize(AbstractRandom random, int size) {
		return Building.randomize(new Random(random.nextLong()), size);
	}
}
