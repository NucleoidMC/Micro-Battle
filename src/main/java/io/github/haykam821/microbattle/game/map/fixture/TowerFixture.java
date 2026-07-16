package io.github.haykam821.microbattle.game.map.fixture;

import io.github.haykam821.microbattle.game.map.fixture.canvas.FixtureCanvas;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xyz.nucleoid.plasmid.api.util.WoodTypeContent;

public class TowerFixture extends Fixture {
	private static final WeightedList<Variant> STATES = WeightedList.<Variant>builder()
		.add(new Variant(WoodTypeContent.OAK, Blocks.SPRUCE_SLAB.defaultBlockState()), 20)
		.add(new Variant(WoodTypeContent.DARK_OAK, Blocks.COBBLED_DEEPSLATE_SLAB.defaultBlockState()), 10)
		.add(new Variant(WoodTypeContent.ACACIA, Blocks.SMOOTH_STONE_SLAB.defaultBlockState()), 5)
		.add(new Variant(WoodTypeContent.JUNGLE, Blocks.CUT_COPPER_SLAB.waxed().unaffected().defaultBlockState()), 1)
		.build();

	private static final BlockState LADDER = Blocks.LADDER.defaultBlockState();

	private final Variant variant;
	private final int height;

	private TowerFixture(Variant variant, int width, int height, int depth) {
		super(width, depth);

		this.height = height;
		this.variant = variant;
	}
	
	@Override
	public void generate(FixtureCanvas canvas, RandomSource random) {
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

	private static int randomizeSize(RandomSource random) {
		return random.nextInt(5) + 5;
	}

	public static TowerFixture randomize(RandomSource random) {
		Variant variant = STATES.getRandom(random).orElseThrow(IllegalStateException::new);
		int depth = TowerFixture.randomizeSize(random);
		
		int width = TowerFixture.randomizeSize(random);
		if (width % 2 == 0) width += 1;

		return new TowerFixture(variant, 5, random.nextInt(4) + 6, depth);
	}

	private static record Variant(WoodTypeContent wood, BlockState slab) {
		public BlockState getPlanks() {
			return this.wood.getPlanks().defaultBlockState();
		}

		public BlockState getLog(Direction.Axis axis) {
			return this.wood.getLog().defaultBlockState()
				.setValue(BlockStateProperties.AXIS, axis);
		}

		public BlockState getFence(boolean xAxis) {
			return this.wood.getFence().defaultBlockState()
				.setValue(BlockStateProperties.NORTH, !xAxis)
				.setValue(BlockStateProperties.EAST, xAxis)
				.setValue(BlockStateProperties.SOUTH, !xAxis)
				.setValue(BlockStateProperties.WEST, xAxis);
		}
	}
}
