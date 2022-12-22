package io.github.haykam821.microbattle.game.map.fixture;

import io.github.haykam821.microbattle.game.map.fixture.canvas.FixtureCanvas;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BuildingFixture extends Fixture {
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

	private final int height;
	private final double vineDensity;
	private final BlockState state;

	private BuildingFixture(int width, int height, int depth, double vineDensity, BlockState state) {
		super(width + 2, depth + 2);

		this.height = height;

		this.vineDensity = vineDensity;
		this.state = state;
	}
	
	@Override
	public void generate(FixtureCanvas canvas, Random random) {
		int width = this.getWidth() - 2;
		int depth = this.getDepth() - 2;

		int startX = 1;
		int startZ = 1;

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < depth; z++) {
				boolean border = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
				for (int y = 0; y < this.height; y++) {
					if (y >= this.height - 2 && !border) continue;
					if (y == this.height - 1 && (x + z) % 2 == 1) continue;

					canvas.setBlockState(x + 1, y, z + 1, this.state);
				}
			}
		}

		if (this.vineDensity > 0) {
			this.generateVineSide(canvas, random, Direction.NORTH, startX, startZ + depth, startX + width - 1, startZ + depth);
			this.generateVineSide(canvas, random, Direction.EAST, startX - 1, startZ, startX - 1, startZ + depth - 1);
			this.generateVineSide(canvas, random, Direction.SOUTH, startX, startZ - 1, startX + width - 1, startZ - 1);
			this.generateVineSide(canvas, random, Direction.WEST, startX + width, startZ, startX + width, startZ + depth - 1);
		}
	}

	private void generateVineSide(FixtureCanvas canvas, Random random, Direction facing, int minX, int minZ, int maxX, int maxZ) {
		BlockState vine = VINE.with(VineBlock.getFacingProperty(facing), true);
		int maxY = this.height - 2;

		for (int y = 0; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (this.vineDensity == 1 || random.nextDouble() < this.vineDensity) {
						canvas.setBlockState(x, y, z, vine);
					}
				}
			}
		}
	}

	private static double getVineDensity(Random random) {
		if (random.nextInt(4) == 0) {
			return random.nextDouble() * 0.6 + 0.3;
		}

		return 0;
	}

	public static BuildingFixture randomize(Random random) {
		int size = random.nextInt(8) + 4;
		if (size % 2 == 0) size += 1;

		double vineDensity = getVineDensity(random);

		BlockState state = STATES.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
		return new BuildingFixture(size, random.nextInt(4) + 6, size, vineDensity, state);
	}
}
