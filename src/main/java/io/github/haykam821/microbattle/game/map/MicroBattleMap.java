package io.github.haykam821.microbattle.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateChunkGenerator;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class MicroBattleMap {
	private final MapTemplate template;
	private final MicroBattleMapConfig config;
	private final BlockBounds floorBounds;
	private final BlockBounds fullBounds;

	public MicroBattleMap(MapTemplate template, MicroBattleMapConfig config, BlockBounds floorBounds, BlockBounds fullBounds) {
		this.template = template;
		this.config = config;

		this.floorBounds = floorBounds;
		this.fullBounds = fullBounds;
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

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}