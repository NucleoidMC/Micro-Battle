package io.github.haykam821.microbattle.game;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.microbattle.game.kit.Kit;
import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class MicroBattleConfig {
	public static final Codec<MicroBattleConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Kit.REGISTRY.listOf().fieldOf("kits").forGetter(MicroBattleConfig::getKits),
			MicroBattleMapConfig.CODEC.fieldOf("map").forGetter(MicroBattleConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(MicroBattleConfig::getPlayerConfig)
		).apply(instance, MicroBattleConfig::new);
	});

	private final List<Kit> kits;
	private final MicroBattleMapConfig mapConfig;
	private final PlayerConfig playerConfig;

	public MicroBattleConfig(List<Kit> kits, MicroBattleMapConfig mapConfig, PlayerConfig playerConfig) {
		this.kits = kits;
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
	}

	public List<Kit> getKits() {
		return this.kits;
	}

	public MicroBattleMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}