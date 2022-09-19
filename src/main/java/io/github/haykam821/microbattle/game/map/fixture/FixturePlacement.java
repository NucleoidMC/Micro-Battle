package io.github.haykam821.microbattle.game.map.fixture;

import net.minecraft.util.math.BlockPos;

public record FixturePlacement(
	Fixture fixture,
	BlockPos start
) {
	
}
