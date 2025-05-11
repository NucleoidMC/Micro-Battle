package io.github.haykam821.microbattle.game.map.fixture;

import net.minecraft.block.Blocks;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public final class Fixtures {
	private static final Pool<FixtureCreator> PRIMARY_FIXTURES = Pool.<FixtureCreator>builder()
		.add(Fixtures::building, 5)
		.add(Fixtures::tower, 1)
		.build();

	private static final Pool<FixtureCreator> DECORATION_FIXTURES = Pool.<FixtureCreator>builder()
		.add(Fixtures::grassPatch, 1)
		.build();

	protected static Fixture building(Random random) {
		return BuildingFixture.randomize(random);
	}

	protected static Fixture tower(Random random) {
		return TowerFixture.randomize(random);
	}

	protected static Fixture primary(Random random) {
		return PRIMARY_FIXTURES.getOrEmpty(random).orElseThrow().get(random);
	}

	protected static Fixture grassPatch(Random random) {
		int radius = random.nextInt(3) + 2;
		return new PatchFixture(radius, BlockStateProvider.of(Blocks.SHORT_GRASS));
	}

	protected static Fixture decoration(Random random) {
		return DECORATION_FIXTURES.getOrEmpty(random).orElseThrow().get(random);
	}

	@FunctionalInterface
	private interface FixtureCreator {
		public Fixture get(Random random);
	}
}
