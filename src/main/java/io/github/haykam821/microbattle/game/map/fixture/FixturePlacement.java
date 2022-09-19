package io.github.haykam821.microbattle.game.map.fixture;

import net.minecraft.util.math.BlockPos;

public record FixturePlacement(
	Fixture fixture,
	BlockPos start
) {
	public boolean intersects(FixturePlacement other) {
		int x = this.start.getX();
		int z = this.start.getZ();

		int otherX = other.start.getX();
		int otherZ = other.start.getZ();

		if (x + this.fixture.getWidth() < otherX) return false;
		if (z + this.fixture.getDepth() < otherZ) return false;

		if (otherX + other.fixture.getWidth() < x) return false;
		if (otherZ + other.fixture.getDepth() < z) return false;

		return true;
	}
}
