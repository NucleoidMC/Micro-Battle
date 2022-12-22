package io.github.haykam821.microbattle.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.microbattle.game.map.fixture.FixtureConfig;

public class MicroBattleMapConfig {
	public static final Codec<MicroBattleMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("x").forGetter(MicroBattleMapConfig::getX),
			Codec.INT.fieldOf("y").forGetter(MicroBattleMapConfig::getY),
			Codec.INT.fieldOf("z").forGetter(MicroBattleMapConfig::getZ),
			Codec.INT.optionalFieldOf("floor_height", 20).forGetter(MicroBattleMapConfig::getFloorHeight),
			Codec.INT.optionalFieldOf("beacon_max_height", 40).forGetter(MicroBattleMapConfig::getFloorHeight),
			Codec.INT.optionalFieldOf("river_radius", 2).forGetter(MicroBattleMapConfig::getRiverRadius),
			Codec.BOOL.optionalFieldOf("deepslate_lava", true).forGetter(MicroBattleMapConfig::hasDeepslateLava),
			FixtureConfig.CODEC.optionalFieldOf("fixtures", FixtureConfig.DEFAULT).forGetter(MicroBattleMapConfig::getFixtureConfig)
		).apply(instance, MicroBattleMapConfig::new);
	});

	private final int x;
	private final int y;
	private final int z;
	private final int floorHeight;
	private final int beaconMaxHeight;
	private final int riverRadius;
	private final boolean deepslateLava;
	private final FixtureConfig fixtureConfig;

	public MicroBattleMapConfig(int x, int y, int z, int floorHeight, int beaconMaxHeight, int riverRadius, boolean deepslateLava, FixtureConfig fixtureConfig) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.floorHeight = floorHeight;
		this.beaconMaxHeight = beaconMaxHeight;
		this.riverRadius = riverRadius;
		this.deepslateLava = deepslateLava;
		this.fixtureConfig = fixtureConfig;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public int getFloorHeight() {
		return this.floorHeight;
	}

	public int getBeaconMaxHeight() {
		return this.beaconMaxHeight;
	}

	public int getRiverRadius() {
		return this.riverRadius;
	}

	public boolean hasDeepslateLava() {
		return this.deepslateLava;
	}

	public FixtureConfig getFixtureConfig() {
		return this.fixtureConfig;
	}
}