package io.github.haykam821.microbattle.game.map;

import java.util.Iterator;
import java.util.Random;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;

public class MicroBattleMapBuilder {
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();

	private final MicroBattleConfig config;

	public MicroBattleMapBuilder(MicroBattleConfig config) {
		this.config = config;
	}

	public MicroBattleMap create() {
		MapTemplate template = MapTemplate.createEmpty();
		MicroBattleMapConfig mapConfig = this.config.getMapConfig();

		BlockBounds floorBounds = BlockBounds.of(BlockPos.ORIGIN, new BlockPos(mapConfig.getX() - 1, mapConfig.getFloorHeight(), mapConfig.getZ() - 1));
		this.build(floorBounds, template, mapConfig);
		this.generateBuildings(floorBounds, template, mapConfig.getPadding());

		BlockBounds fullBounds = BlockBounds.of(floorBounds.min().add(-8, -4, -8), new BlockPos(floorBounds.max().add(8, mapConfig.getY() - mapConfig.getFloorHeight(), 8)));
		return new MicroBattleMap(template, mapConfig, floorBounds, fullBounds);
	}

	private void generateBuildings(BlockBounds floorBounds, MapTemplate template, int padding) {
		Random random = new Random();

		int size = random.nextInt(8) + 4;
		if (size % 2 == 0) size += 1;

		int minY = floorBounds.max().getY();

		int minX = floorBounds.min().getX() + padding;
		int minZ = floorBounds.min().getZ() + padding;

		int maxX = floorBounds.max().getX() - padding + 1;
		int maxZ = floorBounds.max().getZ() - padding + 1;

		// North-west
		Building.randomizeHeight(random, size).generate(template, minX, minY, minZ);

		// North-east
		Building neBuilding = Building.randomizeHeight(random, size);
		neBuilding.generate(template, maxX - neBuilding.getWidth(), minY, minZ);

		// South-west
		Building swBuilding = Building.randomizeHeight(random, size);
		swBuilding.generate(template, minX, minY, maxZ - swBuilding.getDepth());

		// South-east
		Building seBuilding = Building.randomizeHeight(random, size);
		seBuilding.generate(template, maxX - seBuilding.getWidth(), minY, maxZ - seBuilding.getDepth());
	}

	private BlockState getBlockState(BlockPos pos, BlockBounds bounds, int centerX, int minRiverX, int maxRiverX, int centerZ, int minRiverZ, int maxRiverZ, MicroBattleMapConfig mapConfig) {
		int layer = pos.getY() - bounds.min().getY();
		if (layer < mapConfig.getFloorHeight() - 3) {
			return STONE;
		} else if (layer < mapConfig.getFloorHeight() - 1) {
			return DIRT;
		} else if (layer < mapConfig.getFloorHeight()) {
			boolean river = (pos.getX() >= minRiverX && pos.getX() <= maxRiverX) || (pos.getZ() >= minRiverZ && pos.getZ() <= maxRiverZ);
			return river ? WATER : GRASS;
		}
		return null;
	}

	public void build(BlockBounds bounds, MapTemplate template, MicroBattleMapConfig mapConfig) {
		int centerX = bounds.size().getX() / 2;
		int minRiverX = centerX - mapConfig.getRiverRadius() + 1;
		int maxRiverX = centerX + mapConfig.getRiverRadius();

		int centerZ = bounds.size().getZ() / 2;
		int minRiverZ = centerZ - mapConfig.getRiverRadius() + 1;
		int maxRiverZ = centerZ + mapConfig.getRiverRadius();

		Iterator<BlockPos> iterator = bounds.iterator();
		while (iterator.hasNext()) {
			BlockPos pos = iterator.next();

			BlockState state = this.getBlockState(pos, bounds, centerX, minRiverX, maxRiverX, centerZ, minRiverZ, maxRiverZ, mapConfig);
			if (state != null) {
				template.setBlockState(pos, state);
			}
		}
	}
}