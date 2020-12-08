package io.github.haykam821.microbattle.game.map;

import java.util.Iterator;

import io.github.haykam821.microbattle.game.MicroBattleConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class MicroBattleMapBuilder {
	private static final BlockState FLOOR = Blocks.GRASS_BLOCK.getDefaultState();

	private final MicroBattleConfig config;

	public MicroBattleMapBuilder(MicroBattleConfig config) {
		this.config = config;
	}

	public MicroBattleMap create() {
		MapTemplate template = MapTemplate.createEmpty();
		MicroBattleMapConfig mapConfig = this.config.getMapConfig();

		BlockBounds innerBounds = new BlockBounds(BlockPos.ORIGIN, new BlockPos(mapConfig.getX() + 1, 32, mapConfig.getZ() + 1));
		this.build(innerBounds, template, mapConfig);

		BlockBounds fullBounds = new BlockBounds(innerBounds.getMin().add(-8, -4, -8), new BlockPos(innerBounds.getMax().add(8, 0, 8)));
		return new MicroBattleMap(template, innerBounds, fullBounds);
	}

	private BlockState getBlockState(BlockPos pos, BlockBounds bounds, MicroBattleMapConfig mapConfig) {
		int layer = pos.getY() - bounds.getMin().getY();
		return layer == 0 ? FLOOR : null;
	}

	public void build(BlockBounds bounds, MapTemplate template, MicroBattleMapConfig mapConfig) {
		Iterator<BlockPos> iterator = bounds.iterator();
		while (iterator.hasNext()) {
			BlockPos pos = iterator.next();

			BlockState state = this.getBlockState(pos, bounds, mapConfig);
			if (state != null) {
				template.setBlockState(pos, state);
			}
		}
	}
}