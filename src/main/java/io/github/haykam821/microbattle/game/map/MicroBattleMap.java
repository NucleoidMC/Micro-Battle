package io.github.haykam821.microbattle.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

public class MicroBattleMap {
	private final MapTemplate template;
	private final MicroBattleMapConfig config;
	private final BlockBounds floorBounds;
	private final BlockBounds fullBounds;
	private final BlockBounds beaconBounds;

	public MicroBattleMap(MapTemplate template, MicroBattleMapConfig config, BlockBounds floorBounds, BlockBounds fullBounds) {
		this.template = template;
		this.config = config;

		this.floorBounds = floorBounds;
		this.fullBounds = fullBounds;

		BlockPos maxBeaconPos = fullBounds.max().withY(config.getBeaconMaxHeight());
		this.beaconBounds = BlockBounds.of(fullBounds.min(), maxBeaconPos);
	}

	public int getRiverRadius() {
		return this.config.getRiverRadius();
	}

	public BlockBounds getFloorBounds() {
		return this.floorBounds;
	}

	public BlockBounds getFullBounds() {
		return this.fullBounds;
	}

	public BlockBounds getBeaconBounds() {
		return this.beaconBounds;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}