package io.github.haykam821.microbattle.game.map.fixture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FixtureConfig(
	int padding,
	int primary,
	int decorations
) {
	public static final FixtureConfig DEFAULT = new FixtureConfig(6, 1, 50);

	public static final Codec<FixtureConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.optionalFieldOf("padding", DEFAULT.padding).forGetter(FixtureConfig::padding),
			Codec.INT.optionalFieldOf("primary", DEFAULT.primary).forGetter(FixtureConfig::primary),
			Codec.INT.optionalFieldOf("decorations", DEFAULT.decorations).forGetter(FixtureConfig::decorations)
		).apply(instance, FixtureConfig::new);
	});
}