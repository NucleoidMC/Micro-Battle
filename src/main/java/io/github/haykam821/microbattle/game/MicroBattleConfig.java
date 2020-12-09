package io.github.haykam821.microbattle.game;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class MicroBattleConfig {
	public static final Codec<MicroBattleConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			KitType.REGISTRY.listOf().fieldOf("kits").forGetter(MicroBattleConfig::getKits),
			Codec.BOOL.optionalFieldOf("old_combat", false).forGetter(MicroBattleConfig::isOldCombat),
			MicroBattleMapConfig.CODEC.fieldOf("map").forGetter(MicroBattleConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(MicroBattleConfig::getPlayerConfig)
		).apply(instance, MicroBattleConfig::new);
	});

	private final List<KitType<?>> kits;
	private final boolean oldCombat;
	private final MicroBattleMapConfig mapConfig;
	private final PlayerConfig playerConfig;

	public MicroBattleConfig(List<KitType<?>> kits, boolean oldCombat, MicroBattleMapConfig mapConfig, PlayerConfig playerConfig) {
		this.kits = kits;
		this.oldCombat = oldCombat;
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
	}

	public List<KitType<?>> getKits() {
		return this.kits;
	}

	public boolean isOldCombat() {
		return this.oldCombat;
	}

	public MicroBattleMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}