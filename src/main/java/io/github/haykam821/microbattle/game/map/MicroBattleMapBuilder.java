package io.github.haykam821.microbattle.game.map;

import java.util.Optional;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.map.fixture.FixtureArea;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;

public class MicroBattleMapBuilder {
	private static final BlockState DEEPSLATE = Blocks.DEEPSLATE.defaultBlockState();
	private static final BlockState STONE = Blocks.STONE.defaultBlockState();
	private static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.defaultBlockState();

	private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
	private static final BlockState WATER = Blocks.WATER.defaultBlockState();

	private final MicroBattleConfig config;

	public MicroBattleMapBuilder(MicroBattleConfig config) {
		this.config = config;
	}

	public MicroBattleMap create(MinecraftServer server) {
		MapTemplate template = MapTemplate.createEmpty();
		MicroBattleMapConfig mapConfig = this.config.getMapConfig();

		RandomSource random = new XoroshiroRandomSource(RandomSupport.generateUniqueSeed());

		Optional<HolderSet.Named<Biome>> maybeBiomeList = server.registryAccess().lookupOrThrow(Registries.BIOME).get(Main.POTENTIAL_BIOMES);
		if (maybeBiomeList.isPresent()) {
			Optional<Holder<Biome>> maybeBiome = maybeBiomeList.get().getRandomElement(random);
			if (maybeBiome.isPresent()) {
				Optional<ResourceKey<Biome>> maybeKey = maybeBiome.get().unwrapKey();
				if (maybeKey.isPresent()) {
					template.setBiome(maybeKey.get());
				}
			}
		}

		BlockBounds floorBounds = BlockBounds.of(BlockPos.ZERO, new BlockPos(mapConfig.getX() - 1, mapConfig.getFloorHeight(), mapConfig.getZ() - 1));
		this.buildTerrain(floorBounds, template, mapConfig, random);
		FixtureArea.generate(floorBounds, template, random, mapConfig);

		BlockBounds fullBounds = BlockBounds.of(floorBounds.min().offset(-8, -4, -8), new BlockPos(floorBounds.max().offset(8, mapConfig.getY() - mapConfig.getFloorHeight(), 8)));
		return new MicroBattleMap(template, mapConfig, floorBounds, fullBounds);
	}

	private BlockState getDeepslateBlockState(int layer, RandomSource random, boolean bottom, MicroBattleMapConfig mapConfig) {
		if (mapConfig.hasDeepslateLava() && layer < mapConfig.getFloorHeight() - 9 && !bottom && random.nextInt(128) == 0) {
			return LAVA;
		} else {
			return DEEPSLATE;
		}
	}

	private BlockState getBlockState(BlockPos pos, BlockBounds bounds, RandomSource random, boolean bottom, double centerX, int minRiverX, int maxRiverX, double centerZ, int minRiverZ, int maxRiverZ, MicroBattleMapConfig mapConfig) {
		int layer = pos.getY() - bounds.min().getY();
		if (layer < mapConfig.getFloorHeight() - 8) {
			return this.getDeepslateBlockState(layer, random, bottom, mapConfig);
		} else if (layer < mapConfig.getFloorHeight() - 3) {
			return STONE;
		} else if (layer < mapConfig.getFloorHeight() - 1) {
			return DIRT;
		} else if (layer < mapConfig.getFloorHeight()) {
			boolean river = (pos.getX() >= minRiverX && pos.getX() <= maxRiverX) || (pos.getZ() >= minRiverZ && pos.getZ() <= maxRiverZ);
			return river ? WATER : GRASS;
		}
		return null;
	}

	public void buildTerrain(BlockBounds bounds, MapTemplate template, MicroBattleMapConfig mapConfig, RandomSource random) {
		SimplexNoise noiseSampler = new SimplexNoise(random);

		int minY = bounds.min().getY();
		int maxY = bounds.max().getY();

		BlockPos size = bounds.size();

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		double centerX = size.getX() / 2d;
		int minRiverX = (int) centerX - mapConfig.getRiverRadius() + 1;
		int maxRiverX = (int) centerX + mapConfig.getRiverRadius();

		double centerZ = size.getZ() / 2d;
		int minRiverZ = (int) centerZ - mapConfig.getRiverRadius() + 1;
		int maxRiverZ = (int) centerZ + mapConfig.getRiverRadius();

		double denominatorX = 2 * Math.pow(centerX / 2d, 2);
		double denominatorZ = 2 * Math.pow(centerZ / 2d, 2);

		for (int x = 1; x < size.getX(); x += 1) {
			for (int z = 1; z < size.getZ(); z += 1) {
				pos.set(x, maxY, z);

				double exponent = Math.pow(x - centerX, 2) / denominatorX + Math.pow(z - centerZ, 2) / denominatorZ;
				double bell = Math.exp(-exponent) * (size.getY() - 4) + 4;

				double noise = noiseSampler.getValue(x / 20d, z / 20d) * 2;
				double height = Math.max(4, bell + noise);

				for (int y = 0; y < height; y += 1) {
					pos.move(Direction.DOWN);
					if (pos.getY() < minY) break;

					BlockState state = this.getBlockState(pos, bounds, random, y >= height - 4, centerX, minRiverX, maxRiverX, centerZ, minRiverZ, maxRiverZ, mapConfig);
					if (state != null) {
						template.setBlockState(pos, state);
					}
				}
			}
		}
	}
}