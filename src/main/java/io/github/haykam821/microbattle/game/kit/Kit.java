package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import xyz.nucleoid.plasmid.api.game.common.OldCombat;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;
import xyz.nucleoid.stimuli.event.EventResult;

public abstract class Kit {
	protected static final RandomSource RANDOM = RandomSource.createThreadLocalInstance();

	private final KitType<?> type;
	private final List<RestockEntry> restockEntries = new ArrayList<>();
	protected final PlayerEntry entry;
	protected final ServerPlayer player;
	protected final MicroBattleActivePhase phase;

	public Kit(KitType<?> type, PlayerEntry entry) {
		this.type = type;

		this.entry = entry;
		this.player = entry.getPlayer();
		this.phase = entry.getPhase();
	}

	protected boolean addRestockEntry(RestockEntry entry) {
		return this.restockEntries.add(entry);
	}

	protected abstract int getBaseColor();

	protected abstract int getSecondaryColor();

	protected String[] getNeutrals() {
		return new String[0];
	}

	protected String[] getAdvantages() {
		return new String[0];
	}

	protected String[] getDisadvantages() {
		return new String[0];
	}

	private Component getTooltip(String linePrefix) {
		MutableComponent text = Component.literal(linePrefix);
		text.append(Component.literal("• Defeat the other players!").withStyle(ChatFormatting.GRAY));

		for (String line : this.getNeutrals()) {
			text.append(Component.literal("\n" + linePrefix + "• " + line).withStyle(ChatFormatting.GRAY));
		}
		for (String line : this.getAdvantages()) {
			text.append(Component.literal("\n" + linePrefix + "+ " + line).withStyle(ChatFormatting.GREEN));
		}
		for (String line : this.getDisadvantages()) {
			text.append(Component.literal("\n" + linePrefix + "- " + line).withStyle(ChatFormatting.RED));
		}

		return text;
	}

	protected Component getName() {
		return this.type.getName();
	}

	private Component getHoverableName() {
		return this.getName().copy().withStyle(style -> {
			return style.withHoverEvent(new HoverEvent.ShowText(this.getTooltip("")));
		});
	}

	public MutableComponent getReceivedMessage() {
		if (this.entry.getTeamKey() == null) {
			return Component.translatable("text.microbattle.kit_received", this.getHoverableName()).withStyle(ChatFormatting.GRAY);
		} else {
			Component teamName = this.entry.getTeamConfig().name();
			return Component.translatable("text.microbattle.team_kit_received", this.getHoverableName(), teamName).withStyle(ChatFormatting.GRAY);
		}
	}

	public Component getInitialMessage() {
		return this.getReceivedMessage().append("\n").append(this.getTooltip("  "));
	}

	protected ItemStack createArmorStack(Item item, String type, boolean secondary) {
		return ItemStackBuilder.of(item)
			.setDyeColor(secondary ? this.getSecondaryColor() : this.getBaseColor())
			.setName(Component.translatable("text.microbattle.team_armor." + type, this.getName()))
			.setUnbreakable()
			.build();
	}

	protected ItemStack getHelmetStack() {
		return this.createArmorStack(Items.LEATHER_HELMET, "helmet", true);
	}

	protected ItemStack getChestplateStack() {
		return this.createArmorStack(Items.LEATHER_CHESTPLATE, "chestplate", false);
	}

	protected ItemStack getLeggingsStack() {
		return this.createArmorStack(Items.LEATHER_LEGGINGS, "leggings", false);
	}

	protected ItemStack getBootsStack() {
		return this.createArmorStack(Items.LEATHER_BOOTS, "boots", true);
	}

	private List<ItemStack> getArmorStacks() {
		List<ItemStack> armorStacks = new ArrayList<>();
		armorStacks.add(this.getHelmetStack());
		armorStacks.add(this.getChestplateStack());
		armorStacks.add(this.getLeggingsStack());
		armorStacks.add(this.getBootsStack());
		return armorStacks;
	}

	protected void tick() {
		return;
	}

	public final void baseTick() {
		for (RestockEntry entry : this.getRestockEntries()) {
			entry.tick(this.entry);
		}

		if (this.isDamagedByWater() && this.player.isInWaterOrRain()) {
			this.player.hurtServer(this.player.level(), this.player.damageSources().drown(), 1.0F);
		}

		this.tick();
	}

	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.STONE_SWORD);
	}

	protected ItemStack getPickaxeToolStack() {
		return unbreakableStack(Items.STONE_PICKAXE);
	}

	protected ItemStack getAxeToolStack() {
		return unbreakableStack(Items.STONE_AXE);
	}

	protected ItemStack getShovelToolStack() {
		return unbreakableStack(Items.STONE_SHOVEL);
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.BREAD, 8);
	}

	protected void appendCustomInitialStacks(List<ItemStack> stacks) {
		return;
	}

	protected final void appendInitialStacks(List<ItemStack> stacks) {
		addIfNonNull(this::getMainWeaponStack, stacks);
		addIfNonNull(this::getPickaxeToolStack, stacks);
		addIfNonNull(this::getAxeToolStack, stacks);
		addIfNonNull(this::getShovelToolStack, stacks);
		addIfNonNull(this::getFoodStack, stacks);
		
		for (RestockEntry entry : this.getRestockEntries()) {
			addIfNonNull(this.player.registryAccess(), entry::supplyStack, stacks);
		}

		this.appendCustomInitialStacks(stacks);
	}

	public boolean isRespawnPos(BlockPos pos, boolean clear) {
		return false;
	}

	protected Iterable<RestockEntry> getRestockEntries() {
		return this.restockEntries;
	}

	protected MobEffectInstance[] getStatusEffects() {
		return new MobEffectInstance[0];
	}

	public final void applyInventory() {
		entry.getPlayer().getInventory().clearContent();
		if (this.player.containerMenu != null) {
			this.player.containerMenu.setCarried(ItemStack.EMPTY);
		}

		// Add status effects
		for (MobEffectInstance effect : this.getStatusEffects()) {
			player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), true, false));
		}

		List<ItemStack> armorStacks = this.getArmorStacks();
		int slot = EquipmentSlot.HEAD.getIndex(36);
		for (ItemStack stack : armorStacks) {
			player.getInventory().setItem(slot, stack);
			slot -= 1;
		}
	
		List<ItemStack> stacks = new ArrayList<>();
		this.appendInitialStacks(stacks);
		slot = 0;
		for (ItemStack stack : stacks) {
			player.getInventory().setItem(slot, this.phase.isOldCombat() ? OldCombat.applyTo(stack) : stack);
			slot += 1;
		}

		this.entry.updateInventory();
	}

	public final void reinitialize() {
		this.applyInventory();
	}

	public final void initialize() {
		this.reinitialize();
		this.entry.getPlayer().sendSystemMessage(this.getInitialMessage(), false);
	}

	protected void setExperienceBar(float progress) {
		this.player.experienceProgress = Mth.clamp(progress, 0, 1);
		this.player.setExperienceLevels(0);
	}

	public InteractionResult onUseBlock(InteractionHand hand, BlockHitResult hitResult) {
		return InteractionResult.PASS;
	}

	public EventResult afterBlockPlace(BlockPos pos, ItemStack stack, BlockState state) {
		return EventResult.PASS;
	}

	public EventResult onBreakBlock(BlockPos pos) {
		return EventResult.PASS;
	}

	public EventResult onDamaged(PlayerEntry target, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_FIRE) && !this.isDamagedByFire()) {
			return EventResult.DENY;
		}

		return EventResult.PASS;
	}

	public EventResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		return EventResult.PASS;
	}

	public EventResult onDeath(DamageSource source) {
		return EventResult.PASS;
	}

	public EventResult attemptRespawn() {
		return EventResult.PASS;
	}

	public EventResult onKilledPlayer(PlayerEntry entry, DamageSource source) {
		return EventResult.PASS;
	}

	protected static ItemStack unbreakableStack(ItemLike item) {
		return ItemStackBuilder.of(item).setUnbreakable().build();
	}

	public boolean isDamagedByWater() {
		return false;
	}

	public boolean isDamagedByFire() {
		return true;
	}

	/**
	 * Gets the death sound used by this kit.
	 * If {@code null}, uses the default death sound.
	 */
	public SoundEvent getDeathSound() {
		return null;
	}

	/**
	 * Gets the hurt sound used by this kit.
	 * If {@code null}, uses the default hurt sound.
	 */
	public SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	private static void addIfNonNull(HolderLookup.Provider registries, Function<HolderLookup.Provider, ItemStack> supplier, List<ItemStack> stacks) {
		addIfNonNull(() -> supplier.apply(registries), stacks);
	}

	private static void addIfNonNull(Supplier<ItemStack> supplier, List<ItemStack> stacks) {
		ItemStack stack = supplier.get();
		if (stack != null) {
			stacks.add(stack);
		}
	}

	protected static ItemStack createPotionStack(ItemLike item, Optional<Holder<Potion>> maybePotion) {
		return maybePotion
			.map(potion -> PotionContents.createItemStack(item.asItem(), potion))
			.orElseGet(() -> new ItemStack(item));
	}
}
