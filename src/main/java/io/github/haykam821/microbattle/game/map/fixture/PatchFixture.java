package io.github.haykam821.microbattle.game.map.fixture;

import java.util.Random;

import io.github.haykam821.microbattle.game.map.fixture.canvas.FixtureCanvas;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class PatchFixture extends Fixture {
	private final BlockStateProvider provider;

	public PatchFixture(int radius, BlockStateProvider provider) {
		super(radius * 2, radius * 2);

		this.provider = provider;
	}

	@Override
	public void generate(FixtureCanvas canvas) {
		Random random = new Random();

		int radius = this.getWidth() / 2;
		int radius2 = radius * radius;

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				double distance2 = x * x + z * z;
				
				if (random.nextDouble() > distance2 / radius2 - 0.1) {
					canvas.setBlockState(radius + x, 0, radius + z, this.provider, random);
				}
			}
		}
	}
}
