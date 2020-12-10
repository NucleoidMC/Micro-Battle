package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class RespawnerKit extends PlayerKit {
	private BlockPos respawnPos;

	public RespawnerKit(PlayerEntry entry) {
		super(KitTypes.RESPAWNER, entry);
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Place down your beacon in a safe place",
			"You will respawn from this beacon if you die",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You won't respawn if your beacon is broken",
		};
	}

	@Override
	protected ItemStack getChestplateStack() {
		return this.createArmorStack(Items.CHAINMAIL_CHESTPLATE, "chestplate", false);
	}

	@Override
	protected void appendInitialStacks(List<ItemStack> stacks) {
		super.appendInitialStacks(stacks);
		if (this.respawnPos == null) {
			stacks.add(new ItemStack(Items.BEACON));
		}
	}

	public BlockPos getRespawnPos() {
		return this.respawnPos;
	}

	public void setRespawnPos(BlockPos respawnPos) {
		this.respawnPos = respawnPos;
	}
}
