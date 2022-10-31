package io.github.haykam821.microbattle.game.map.fixture;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import io.github.haykam821.microbattle.game.map.fixture.canvas.TemplateFixtureCanvas;
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

	private final int padding;

	public FixtureArea(int minX, int minZ, int y, int maxX, int maxZ, int padding) {
		this.minX = minX;
		this.minZ = minZ;

		this.y = y;

		this.maxX = maxX;
		this.maxZ = maxZ;

		this.padding = padding;
	}

	public FixturePlacement place(Fixture fixture, AbstractRandom random, boolean padded) {
		int padding = padded ? this.padding : 1;
		
		int minX = this.minX + padding;
		int minZ = this.minZ + padding;

		int x = random.nextBetween(0, this.maxX - padding - fixture.getWidth() - minX);
		int z = random.nextBetween(0, this.maxZ - padding - fixture.getDepth() - minZ);

		BlockPos start = new BlockPos(minX + x, this.y, minZ + z);

		FixturePlacement placement = new FixturePlacement(fixture, start);

		for (FixturePlacement other : this.placements) {
			if (placement.intersects(other)) {
				return null;
			}
		}

		this.placements.add(placement);

		return placement;
	}

	public void generate(MapTemplate template) {
		TemplateFixtureCanvas canvas = new TemplateFixtureCanvas(template);

		for (FixturePlacement placement : this.placements) {
			canvas.setStart(placement.start());
			placement.fixture().generate(canvas);
		}
	}

	public static void generate(BlockBounds floorBounds, MapTemplate template, AbstractRandom abstractRandom, MicroBattleMapConfig mapConfig) {
		Random random = new Random(abstractRandom.nextLong());
		FixtureConfig config = mapConfig.getFixtureConfig();

		// Calculate positioning
		BlockPos size = floorBounds.size();

		double centerX = size.getX() / 2d;
		double centerZ = size.getZ() / 2d;

		int y = floorBounds.max().getY();
		int padding = config.padding();

		int minX = floorBounds.min().getX();
		int minZ = floorBounds.min().getZ();

		int minRiverX = (int) centerX - mapConfig.getRiverRadius() + 1;
		int minRiverZ = (int) centerZ - mapConfig.getRiverRadius() + 1;

		int maxRiverX = (int) centerX + mapConfig.getRiverRadius();
		int maxRiverZ = (int) centerZ + mapConfig.getRiverRadius();

		int maxX = floorBounds.max().getX();
		int maxZ = floorBounds.max().getZ();

		// Add areas
		Set<FixtureArea> areas = new HashSet<>();

		areas.add(new FixtureArea(minX, minZ, y, minRiverX, minRiverZ, padding));
		areas.add(new FixtureArea(maxRiverX, minZ, y, maxX, minRiverZ, padding));
		areas.add(new FixtureArea(minX, maxRiverZ, y, minRiverX, maxZ, padding));
		areas.add(new FixtureArea(maxRiverX, maxRiverZ, y, maxX, maxZ, padding));

		// Generate areas
		for (FixtureArea area : areas) {
			for (int index = 0; index < config.primary(); index++) {
				area.place(Fixtures.primary(random), abstractRandom, true);
			}

			for (int index = 0; index < config.decorations(); index++) {
				area.place(Fixtures.decoration(random), abstractRandom, false);
			}

			area.generate(template);
		}
	}
}
