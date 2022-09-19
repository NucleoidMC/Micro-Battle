package io.github.haykam821.microbattle.game.map.fixture;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public final class Fixtures {
	private static final DataPool<FixtureCreator> DECORATION_FIXTURES = DataPool.<FixtureCreator>builder()
		.add(Fixtures::grassPatch, 1)
		.build();

	protected static Fixture building(Random random) {
		return BuildingFixture.randomize(random);
	}

	protected static Fixture grassPatch(Random random) {
		int radius = random.nextInt(3) + 2;
		return new PatchFixture(radius, BlockStateProvider.of(Blocks.GRASS));
	}

	protected static Fixture decoration(Random random) {
		return DECORATION_FIXTURES.getDataOrEmpty(random).orElseThrow().get(random);
	}

	@FunctionalInterface
	private interface FixtureCreator {
		public Fixture get(Random random);
	}
}
