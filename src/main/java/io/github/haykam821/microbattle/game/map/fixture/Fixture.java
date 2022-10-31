package io.github.haykam821.microbattle.game.map.fixture;

import io.github.haykam821.microbattle.game.map.fixture.canvas.FixtureCanvas;

public abstract class Fixture {
	private final int width;
	private final int depth;

	public Fixture(int width, int depth) {
		this.width = width;
		this.depth = depth;
	}

	public abstract void generate(FixtureCanvas canvas);

	public int getWidth() {
		return this.width;
	}

	public int getDepth() {
		return this.depth;
	}
}
