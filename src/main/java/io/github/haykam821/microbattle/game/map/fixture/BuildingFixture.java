package io.github.haykam821.microbattle.game.map.fixture;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.random.AbstractRandom;
import xyz.nucleoid.map_templates.MapTemplate;

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
		super(width, depth);

		this.height = height;

		this.vineDensity = vineDensity;
		this.state = state;
	}
	
	@Override
	public void generate(MapTemplate template, BlockPos start) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		Random random = new Random();

		int width = this.getWidth();
		int depth = this.getDepth();

		int startX = start.getX();
		int startY = start.getY();
		int startZ = start.getZ();

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < depth; z++) {
				boolean border = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
				for (int y = 0; y < this.height; y++) {
					if (y >= this.height - 2 && !border) continue;
					if (y == this.height - 1 && (x + z) % 2 == 1) continue;

					pos.set(startX + x, startY + y, startZ + z);
					template.setBlockState(pos, this.state);
				}
			}
		}

		if (this.vineDensity > 0) {
			this.generateVineSide(template, pos, random, Direction.NORTH, startX, startY, startZ + depth, startX + width - 1, startY + this.height - 2, startZ + depth);
			this.generateVineSide(template, pos, random, Direction.EAST, startX - 1, startY, startZ, startX - 1, startY + this.height - 2, startZ + depth - 1);
			this.generateVineSide(template, pos, random, Direction.SOUTH, startX, startY, startZ - 1, startX + width - 1, startY + this.height - 2, startZ - 1);
			this.generateVineSide(template, pos, random, Direction.WEST, startX + width, startY, startZ, startX + width, startY + this.height - 2, startZ + depth - 1);
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

	public static BuildingFixture randomize(Random random) {
		int size = random.nextInt(8) + 4;
		if (size % 2 == 0) size += 1;

		double vineDensity = random.nextInt(4) == 0 ? random.nextDouble(0.3, 0.9) : 0;

		BlockState state = STATES.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
		return new BuildingFixture(size, random.nextInt(4) + 6, size, vineDensity, state);
	}

	public static BuildingFixture randomize(AbstractRandom random) {
		return BuildingFixture.randomize(new Random(random.nextLong()));
	}
}
