package io.github.haykam821.microbattle.game.map.fixture;

import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.MapTemplate;

public abstract class Fixture {
	private final int width;
	private final int depth;

	public Fixture(int width, int depth) {
		this.width = width;
		this.depth = depth;
	}

	public abstract void generate(MapTemplate template, BlockPos start);

	public int getWidth() {
		return this.width;
	}

	public int getDepth() {
		return this.depth;
	}
}
