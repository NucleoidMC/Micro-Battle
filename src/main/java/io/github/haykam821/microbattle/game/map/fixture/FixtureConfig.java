package io.github.haykam821.microbattle.game.map.fixture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FixtureConfig(
	int padding,
	int buildings
) {
	public static final FixtureConfig DEFAULT = new FixtureConfig(6, 1);

	public static final Codec<FixtureConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.optionalFieldOf("padding", DEFAULT.padding).forGetter(FixtureConfig::padding),
			Codec.INT.optionalFieldOf("buildings", DEFAULT.buildings).forGetter(FixtureConfig::buildings)
		).apply(instance, FixtureConfig::new);
	});
}