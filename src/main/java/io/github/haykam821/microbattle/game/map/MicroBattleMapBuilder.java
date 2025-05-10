package io.github.haykam821.microbattle.game.map;

import java.util.Optional;

import io.github.haykam821.microbattle.Main;
import io.github.haykam821.microbattle.game.MicroBattleConfig;
import io.github.haykam821.microbattle.game.map.fixture.FixtureArea;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.biome.Biome;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;

public class MicroBattleMapBuilder {
	private static final BlockState DEEPSLATE = Blocks.DEEPSLATE.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();

	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();

	private final MicroBattleConfig config;

	public MicroBattleMapBuilder(MicroBattleConfig config) {
		this.config = config;
	}

	public MicroBattleMap create(MinecraftServer server) {
		MapTemplate template = MapTemplate.createEmpty();
		MicroBattleMapConfig mapConfig = this.config.getMapConfig();

		Random random = new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed());

		Optional<RegistryEntryList.Named<Biome>> maybeBiomeList = server.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOptional(Main.POTENTIAL_BIOMES);
		if (maybeBiomeList.isPresent()) {
			Optional<RegistryEntry<Biome>> maybeBiome = maybeBiomeList.get().getRandom(random);
			if (maybeBiome.isPresent()) {
				Optional<RegistryKey<Biome>> maybeKey = maybeBiome.get().getKey();
				if (maybeKey.isPresent()) {
					template.setBiome(maybeKey.get());
				}
			}
		}

		BlockBounds floorBounds = BlockBounds.of(BlockPos.ORIGIN, new BlockPos(mapConfig.getX() - 1, mapConfig.getFloorHeight(), mapConfig.getZ() - 1));
		this.buildTerrain(floorBounds, template, mapConfig, random);
		FixtureArea.generate(floorBounds, template, random, mapConfig);

		BlockBounds fullBounds = BlockBounds.of(floorBounds.min().add(-8, -4, -8), new BlockPos(floorBounds.max().add(8, mapConfig.getY() - mapConfig.getFloorHeight(), 8)));
		return new MicroBattleMap(template, mapConfig, floorBounds, fullBounds);
	}

	private BlockState getDeepslateBlockState(int layer, Random random, boolean bottom, MicroBattleMapConfig mapConfig) {
		if (mapConfig.hasDeepslateLava() && layer < mapConfig.getFloorHeight() - 9 && !bottom && random.nextInt(128) == 0) {
			return LAVA;
		} else {
			return DEEPSLATE;
		}
	}

	private BlockState getBlockState(BlockPos pos, BlockBounds bounds, Random random, boolean bottom, double centerX, int minRiverX, int maxRiverX, double centerZ, int minRiverZ, int maxRiverZ, MicroBattleMapConfig mapConfig) {
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

	public void buildTerrain(BlockBounds bounds, MapTemplate template, MicroBattleMapConfig mapConfig, Random random) {
		SimplexNoiseSampler noiseSampler = new SimplexNoiseSampler(random);

		int minY = bounds.min().getY();
		int maxY = bounds.max().getY();

		BlockPos size = bounds.size();

		BlockPos.Mutable pos = new BlockPos.Mutable();

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

				double noise = noiseSampler.sample(x / 20d, z / 20d) * 2;
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