package io.github.haykam821.microbattle.game.map.fixture;

import io.github.haykam821.microbattle.game.map.fixture.canvas.FixtureCanvas;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.plasmid.api.util.WoodType;

public class TowerFixture extends Fixture {
	private static final DataPool<Variant> STATES = DataPool.<Variant>builder()
		.add(new Variant(WoodType.OAK, Blocks.SPRUCE_SLAB.getDefaultState()), 20)
		.add(new Variant(WoodType.DARK_OAK, Blocks.COBBLED_DEEPSLATE_SLAB.getDefaultState()), 10)
		.add(new Variant(WoodType.ACACIA, Blocks.SMOOTH_STONE_SLAB.getDefaultState()), 5)
		.add(new Variant(WoodType.JUNGLE, Blocks.WAXED_CUT_COPPER_SLAB.getDefaultState()), 1)
		.build();

	private static final BlockState LADDER = Blocks.LADDER.getDefaultState();

	private final Variant variant;
	private final int height;

	private TowerFixture(Variant variant, int width, int height, int depth) {
		super(width, depth);

		this.height = height;
		this.variant = variant;
	}
	
	@Override
	public void generate(FixtureCanvas canvas, Random random) {
		BlockState planks = this.variant.getPlanks();

		BlockState xAxisLog = this.variant.getLog(Direction.Axis.X);
		BlockState verticalLog = this.variant.getLog(Direction.Axis.Y);
		BlockState zAxisLog = this.variant.getLog(Direction.Axis.Z);

		BlockState xAxisFence = this.variant.getFence(true);
		BlockState zAxisFence = this.variant.getFence(false);

		int maxX = this.getWidth() - 1;
		int maxZ = this.getDepth() - 1;

		int centerX = this.getWidth() / 2;

		for (int x = 0; x <= maxX; x++) {
			for (int z = 1; z <= maxZ; z++) {
				if ((x == 0 || x == maxX) && (z == 1 || z == maxZ)) {
					for (int y = 0; y < this.height; y++) {
						canvas.setBlockState(x, y, z, verticalLog);
					}

					canvas.setBlockState(x, this.height, z, this.variant.slab());
				} else if (x == 0 || x == maxX) {
					canvas.setBlockState(x, this.height - 2, z, zAxisLog);
					canvas.setBlockState(x, this.height - 3, z, zAxisFence);
				} else if (z == 1) {
					canvas.setBlockState(x, this.height - 2, z, xAxisLog);

					for (int y = 0; y < this.height - 2; y++) {
						canvas.setBlockState(x, y, z, x == centerX ? planks : xAxisFence);
					}
				} else if (z == maxZ) {
					canvas.setBlockState(x, this.height - 2, z, xAxisLog);
					canvas.setBlockState(x, this.height - 3, z, xAxisFence);
				} else {
					canvas.setBlockState(x, this.height - 2, z, planks);
				}
			}
		}

		for (int y = 0; y < this.height - 1; y++) {
			canvas.setBlockState(centerX, y, 0, LADDER);
		}
	}

	private static int randomizeSize(Random random) {
		return random.nextInt(5) + 5;
	}

	public static TowerFixture randomize(Random random) {
		Variant variant = STATES.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
		int depth = TowerFixture.randomizeSize(random);
		
		int width = TowerFixture.randomizeSize(random);
		if (width % 2 == 0) width += 1;

		return new TowerFixture(variant, 5, random.nextInt(4) + 6, depth);
	}

	private static record Variant(WoodType wood, BlockState slab) {
		public BlockState getPlanks() {
			return this.wood.getPlanks().getDefaultState();
		}

		public BlockState getLog(Direction.Axis axis) {
			return this.wood.getLog().getDefaultState()
				.with(Properties.AXIS, axis);
		}

		public BlockState getFence(boolean xAxis) {
			return this.wood.getFence().getDefaultState()
				.with(Properties.NORTH, !xAxis)
				.with(Properties.EAST, xAxis)
				.with(Properties.SOUTH, !xAxis)
				.with(Properties.WEST, xAxis);
		}
	}
}
