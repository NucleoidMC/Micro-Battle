package io.github.haykam821.microbattle.game.map.fixture;

import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public final class Fixtures {
	private static final WeightedList<FixtureCreator> PRIMARY_FIXTURES = WeightedList.<FixtureCreator>builder()
		.add(Fixtures::building, 5)
		.add(Fixtures::tower, 1)
		.build();

	private static final WeightedList<FixtureCreator> DECORATION_FIXTURES = WeightedList.<FixtureCreator>builder()
		.add(Fixtures::grassPatch, 1)
		.build();

	protected static Fixture building(RandomSource random) {
		return BuildingFixture.randomize(random);
	}

	protected static Fixture tower(RandomSource random) {
		return TowerFixture.randomize(random);
	}

	protected static Fixture primary(RandomSource random) {
		return PRIMARY_FIXTURES.getRandom(random).orElseThrow().get(random);
	}

	protected static Fixture grassPatch(RandomSource random) {
		int radius = random.nextInt(3) + 2;
		return new PatchFixture(radius, BlockStateProvider.simple(Blocks.SHORT_GRASS));
	}

	protected static Fixture decoration(RandomSource random) {
		return DECORATION_FIXTURES.getRandom(random).orElseThrow().get(random);
	}

	@FunctionalInterface
	private interface FixtureCreator {
		public Fixture get(RandomSource random);
	}
}
