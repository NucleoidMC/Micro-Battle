package io.github.haykam821.microbattle.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MicroBattleMapConfig {
	public static final Codec<MicroBattleMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("x").forGetter(MicroBattleMapConfig::getX),
			Codec.INT.fieldOf("y").forGetter(MicroBattleMapConfig::getY),
			Codec.INT.fieldOf("z").forGetter(MicroBattleMapConfig::getZ),
			Codec.INT.optionalFieldOf("floor_height", 6).forGetter(MicroBattleMapConfig::getFloorHeight)
		).apply(instance, MicroBattleMapConfig::new);
	});

	private final int x;
	private final int y;
	private final int z;
	private final int floorHeight;

	public MicroBattleMapConfig(int x, int y, int z, int floorHeight) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.floorHeight = floorHeight;
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
}