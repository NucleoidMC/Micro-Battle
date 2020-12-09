package io.github.haykam821.microbattle.game;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.player.GameTeam;

public class MicroBattleConfig {
	public static final Codec<MicroBattleConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			KitType.REGISTRY.listOf().fieldOf("kits").forGetter(MicroBattleConfig::getKits),
			GameTeam.CODEC.listOf().optionalFieldOf("teams").forGetter(MicroBattleConfig::getTeams),
			Codec.BOOL.optionalFieldOf("old_combat", false).forGetter(MicroBattleConfig::isOldCombat),
			MicroBattleMapConfig.CODEC.fieldOf("map").forGetter(MicroBattleConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(MicroBattleConfig::getPlayerConfig)
		).apply(instance, MicroBattleConfig::new);
	});

	private final List<KitType<?>> kits;
	private final Optional<List<GameTeam>> teams;
	private final boolean oldCombat;
	private final MicroBattleMapConfig mapConfig;
	private final PlayerConfig playerConfig;

	public MicroBattleConfig(List<KitType<?>> kits, Optional<List<GameTeam>> teams, boolean oldCombat, MicroBattleMapConfig mapConfig, PlayerConfig playerConfig) {
		this.kits = kits;
		this.teams = teams;
		this.oldCombat = oldCombat;
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
	}

	public List<KitType<?>> getKits() {
		return this.kits;
	}

	public Optional<List<GameTeam>> getTeams() {
		return this.teams;
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