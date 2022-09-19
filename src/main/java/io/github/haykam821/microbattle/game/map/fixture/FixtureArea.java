package io.github.haykam821.microbattle.game.map.fixture;

import java.util.HashSet;
import java.util.Set;

import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.random.AbstractRandom;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;

public class FixtureArea {
	private final Set<FixturePlacement> placements = new HashSet<>();

	private final int minX;
	private final int minZ;

	private final int y;

	private final int maxX;
	private final int maxZ;

	public FixtureArea(int minX, int minZ, int y, int maxX, int maxZ) {
		this.minX = minX;
		this.minZ = minZ;

		this.y = y;

		this.maxX = maxX;
		this.maxZ = maxZ;
	}

	public FixturePlacement place(Fixture fixture, AbstractRandom random) {
		int x = random.nextBetween(0, this.maxX - fixture.getWidth() - this.minX);
		int z = random.nextBetween(0, this.maxZ - fixture.getDepth() - this.minZ);

		BlockPos start = new BlockPos(this.minX + x, this.y, this.minZ + z);

		FixturePlacement placement = new FixturePlacement(fixture, start);
		this.placements.add(placement);

		return placement;
	}

	public void generate(MapTemplate template) {
		for (FixturePlacement placement : this.placements) {
			placement.fixture().generate(template, placement.start());
		}
	}

	public static void generate(BlockBounds floorBounds, MapTemplate template, AbstractRandom random, MicroBattleMapConfig config) {
		// Calculate positioning
		BlockPos size = floorBounds.size();

		double centerX = size.getX() / 2d;
		double centerZ = size.getZ() / 2d;

		int y = floorBounds.max().getY();
		int padding = config.getPadding();

		int minX = floorBounds.min().getX();
		int minZ = floorBounds.min().getZ();

		int minRiverX = (int) centerX - config.getRiverRadius() + 1;
		int minRiverZ = (int) centerZ - config.getRiverRadius() + 1;

		int maxRiverX = (int) centerX + config.getRiverRadius();
		int maxRiverZ = (int) centerZ + config.getRiverRadius();

		int maxX = floorBounds.max().getX();
		int maxZ = floorBounds.max().getZ();

		// Add areas
		Set<FixtureArea> areas = new HashSet<>();

		areas.add(new FixtureArea(minX + padding, minZ + padding, y, minRiverX - padding, minRiverZ - padding));
		areas.add(new FixtureArea(maxRiverX + padding, minZ + padding, y, maxX - padding, minRiverZ - padding));
		areas.add(new FixtureArea(minX + padding, maxRiverZ + padding, y, minRiverX - padding, maxZ - padding));
		areas.add(new FixtureArea(maxRiverX + padding, maxRiverZ + padding, y, maxX - padding, maxZ - padding));

		// Generate areas
		for (FixtureArea area : areas) {
			area.place(BuildingFixture.randomize(random), random);
			area.generate(template);
		}
	}
}
