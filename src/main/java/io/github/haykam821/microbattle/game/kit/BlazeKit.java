package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;

public class BlazeKit extends Kit {
	public BlazeKit(PlayerEntry entry) {
		super(KitTypes.BLAZE, entry);
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You are not damaged by fire",
			"When below half health or on fire, your attacks will engulf others in flames",
		};
	}
	
	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"Water is harmful to you",
		};
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.ORANGE.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.YELLOW.getFireworkColor();
	}

	@Override
	public boolean isDamagedByWater() {
		return true;
	}

	@Override
	public boolean isDamagedByFire() {
		return false;
	}

	@Override
	public ActionResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		if (this.player.isOnFire()) {
			target.getPlayer().setOnFireFor((int) amount);
		}
		return ActionResult.PASS;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.player.getHealth() < this.player.getMaxHealth() / 2 && this.player.getFireTicks() < 5) {
			this.player.setFireTicks(5);
		}
	}
}
