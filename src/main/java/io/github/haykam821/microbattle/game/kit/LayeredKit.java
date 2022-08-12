package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class LayeredKit extends Kit {
	private final Kit base;
	private final Kit layer;

	public LayeredKit(Kit base, Kit layer, PlayerEntry entry) {
		super(null, entry);

		this.base = base;
		this.layer = layer;
	}

	@Override
	protected int getBaseColor() {
		return this.base.getBaseColor();
	}

	@Override
	protected int getSecondaryColor() {
		return this.base.getSecondaryColor();
	}

	@Override
	protected String[] getNeutrals() {
		return ArrayUtils.addAll(this.base.getNeutrals(), this.layer.getNeutrals());
	}

	@Override
	protected String[] getAdvantages() {
		return ArrayUtils.addAll(this.base.getAdvantages(), this.layer.getAdvantages());
	}

	@Override
	protected String[] getDisadvantages() {
		return ArrayUtils.addAll(this.base.getDisadvantages(), this.layer.getDisadvantages());
	}

	@Override
	protected Text getName() {
		return this.base.getName().copy().append(" ").append(this.layer.getName());
	}

	@Override
	protected ItemStack getHelmetStack() {
		return this.base.getHelmetStack();
	}

	@Override
	protected ItemStack getChestplateStack() {
		return this.base.getChestplateStack();
	}

	@Override
	protected ItemStack getLeggingsStack() {
		return this.base.getLeggingsStack();
	}

	@Override
	protected ItemStack getBootsStack() {
		return this.base.getBootsStack();
	}

	@Override
	public void tick() {
		this.base.tick();
		this.layer.tick();
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return this.base.getMainWeaponStack();
	}

	@Override
	protected ItemStack getPickaxeToolStack() {
		return this.base.getPickaxeToolStack();
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return this.base.getAxeToolStack();
	}

	@Override
	protected ItemStack getShovelToolStack() {
		return this.base.getShovelToolStack();
	}

	@Override
	protected ItemStack getFoodStack() {
		return this.base.getFoodStack();
	}

	@Override
	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		this.base.appendCustomInitialStacks(stacks);
		this.layer.appendCustomInitialStacks(stacks);
	}

	@Override
	public boolean isRespawnPos(BlockPos pos) {
		return this.base.isRespawnPos(pos) || this.layer.isRespawnPos(pos);
	}

	@Override
	protected Iterable<RestockEntry> getRestockEntries() {
		return Iterables.concat(this.base.getRestockEntries(), this.layer.getRestockEntries());
	}

	@Override
	protected StatusEffectInstance[] getStatusEffects() {
		return ArrayUtils.addAll(this.base.getStatusEffects(), this.layer.getStatusEffects());
	}

	@Override
	public ActionResult onUseBlock(Hand hand, BlockHitResult hitResult) {
		ActionResult result = this.base.onUseBlock(hand, hitResult);
		return result == ActionResult.PASS ? this.layer.onUseBlock(hand, hitResult) : result;
	}

	@Override
	public ActionResult afterBlockPlace(BlockPos pos, ItemStack stack, BlockState state) {
		ActionResult result = this.base.afterBlockPlace(pos, stack, state);
		return result == ActionResult.PASS ? this.layer.afterBlockPlace(pos, stack, state) : result;
	}

	@Override
	public ActionResult onBreakBlock(BlockPos pos) {
		ActionResult result = this.base.onBreakBlock(pos);
		return result == ActionResult.PASS ? this.layer.onBreakBlock(pos) : result;
	}

	@Override
	public ActionResult onDamaged(PlayerEntry target, DamageSource source, float amount) {
		ActionResult result = this.base.onDamaged(target, source, amount);
		return result == ActionResult.PASS ? this.layer.onDamaged(target, source, amount) : result;
	}

	@Override
	public ActionResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		ActionResult result = this.base.onDealDamage(target, source, amount);
		return result == ActionResult.PASS ? this.layer.onDealDamage(target, source, amount) : result;
	}

	@Override
	public ActionResult onDeath(DamageSource source) {
		ActionResult result = this.base.onDeath(source);
		return result == ActionResult.PASS ? this.layer.onDeath(source) : result;
	}

	@Override
	public ActionResult attemptRespawn() {
		ActionResult result = this.base.attemptRespawn();
		return result == ActionResult.PASS ? this.layer.attemptRespawn() : result;
	}

	@Override
	public ActionResult onKilledPlayer(PlayerEntry entry, DamageSource source) {
		ActionResult result = this.base.onKilledPlayer(entry, source);
		return result == ActionResult.PASS ? this.layer.onKilledPlayer(entry, source) : result;
	}

	@Override
	public boolean isDamagedByFire() {
		return this.base.isDamagedByFire() && this.layer.isDamagedByFire();
	}

	@Override
	public boolean isDamagedByWater() {
		return this.base.isDamagedByWater() && this.layer.isDamagedByWater();
	}

	@Override
	public SoundEvent getDeathSound() {
		return this.base.getDeathSound();
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return this.base.getHurtSound(source);
	}
}
