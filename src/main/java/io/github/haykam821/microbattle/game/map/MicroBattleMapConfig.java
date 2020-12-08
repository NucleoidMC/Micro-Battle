package io.github.haykam821.microbattle.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MicroBattleMapConfig {
	public static final Codec<MicroBattleMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("x").forGetter(MicroBattleMapConfig::getX),
			Codec.INT.fieldOf("z").forGetter(MicroBattleMapConfig::getZ),
			Codec.BOOL.optionalFieldOf("walls", true).forGetter(MicroBattleMapConfig::hasWalls)
		).apply(instance, MicroBattleMapConfig::new);
	});

	private final int x;
	private final int z;
	private final boolean walls;

	public MicroBattleMapConfig(int x, int z, boolean walls) {
		this.x = x;
		this.z = z;
		this.walls = walls;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public boolean hasWalls() {
		return this.walls;
	}
}