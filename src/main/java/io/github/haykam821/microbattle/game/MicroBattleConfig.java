package io.github.haykam821.microbattle.game;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.microbattle.game.kit.KitPreset;
import io.github.haykam821.microbattle.game.kit.KitType;
import io.github.haykam821.microbattle.game.map.MicroBattleMapConfig;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamList;

public class MicroBattleConfig {
	public static final MapCodec<MicroBattleConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
			KitPreset.EITHER_CODEC.fieldOf("kits").forGetter(config -> Either.right(config.getKits())),
			KitType.REGISTRY.optionalFieldOf("layer_kit").forGetter(MicroBattleConfig::getLayerKit),
			GameTeamList.CODEC.optionalFieldOf("teams").forGetter(MicroBattleConfig::getTeams),
			Codec.BOOL.optionalFieldOf("old_combat", false).forGetter(MicroBattleConfig::isOldCombat),
			MicroBattleMapConfig.CODEC.fieldOf("map").forGetter(MicroBattleConfig::getMapConfig),
			WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(MicroBattleConfig::getPlayerConfig),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("ticks_until_close", ConstantIntProvider.create(SharedConstants.TICKS_PER_SECOND * 5)).forGetter(MicroBattleConfig::getTicksUntilClose)
		).apply(instance, MicroBattleConfig::new);
	});

	private final List<KitType<?>> kits;
	private final Optional<KitType<?>> layerKit;
	private final Optional<GameTeamList> teams;
	private final boolean oldCombat;
	private final MicroBattleMapConfig mapConfig;
	private final WaitingLobbyConfig playerConfig;
	private final IntProvider ticksUntilClose;

	public MicroBattleConfig(Either<List<KitType<?>>, List<KitType<?>>> kits, Optional<KitType<?>> layerKit, Optional<GameTeamList> teams, boolean oldCombat, MicroBattleMapConfig mapConfig, WaitingLobbyConfig playerConfig, IntProvider ticksUntilClose) {
		this.kits = kits.left().isPresent() ? kits.left().get() : kits.right().get();
		this.layerKit = layerKit;
		this.teams = teams;
		this.oldCombat = oldCombat;
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
		this.ticksUntilClose = ticksUntilClose;
	}

	public List<KitType<?>> getKits() {
		return this.kits;
	}

	public Optional<KitType<?>> getLayerKit() {
		return this.layerKit;
	}

	public Optional<GameTeamList> getTeams() {
		return this.teams;
	}

	public boolean isOldCombat() {
		return this.oldCombat;
	}

	public MicroBattleMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public WaitingLobbyConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public IntProvider getTicksUntilClose() {
		return this.ticksUntilClose;
	}
}