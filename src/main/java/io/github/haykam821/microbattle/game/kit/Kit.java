package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.plasmid.api.game.common.OldCombat;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;
import xyz.nucleoid.stimuli.event.EventResult;

public abstract class Kit {
	protected static final Random RANDOM = Random.createLocal();

	private final KitType<?> type;
	private final List<RestockEntry> restockEntries = new ArrayList<>();
	protected final PlayerEntry entry;
	protected final ServerPlayerEntity player;
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

	private Text getTooltip(String linePrefix) {
		MutableText text = Text.literal(linePrefix);
		text.append(Text.literal("• Defeat the other players!").formatted(Formatting.GRAY));

		for (String line : this.getNeutrals()) {
			text.append(Text.literal("\n" + linePrefix + "• " + line).formatted(Formatting.GRAY));
		}
		for (String line : this.getAdvantages()) {
			text.append(Text.literal("\n" + linePrefix + "+ " + line).formatted(Formatting.GREEN));
		}
		for (String line : this.getDisadvantages()) {
			text.append(Text.literal("\n" + linePrefix + "- " + line).formatted(Formatting.RED));
		}

		return text;
	}

	protected Text getName() {
		return this.type.getName();
	}

	private Text getHoverableName() {
		return this.getName().copy().styled(style -> {
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.getTooltip("")));
		});
	}

	public MutableText getReceivedMessage() {
		if (this.entry.getTeamKey() == null) {
			return Text.translatable("text.microbattle.kit_received", this.getHoverableName()).formatted(Formatting.GRAY);
		} else {
			Text teamName = this.entry.getTeamConfig().name();
			return Text.translatable("text.microbattle.team_kit_received", this.getHoverableName(), teamName).formatted(Formatting.GRAY);
		}
	}

	public Text getInitialMessage() {
		return this.getReceivedMessage().append("\n").append(this.getTooltip("  "));
	}

	protected ItemStack createArmorStack(Item item, String type, boolean secondary) {
		return ItemStackBuilder.of(item)
			.setDyeColor(secondary ? this.getSecondaryColor() : this.getBaseColor())
			.setName(Text.translatable("text.microbattle.team_armor." + type, this.getName()))
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

		if (this.isDamagedByWater() && this.player.isWet()) {
			this.player.damage(this.player.getServerWorld(), this.player.getDamageSources().drown(), 1.0F);
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
			addIfNonNull(this.player.getRegistryManager(), entry::supplyStack, stacks);
		}

		this.appendCustomInitialStacks(stacks);
	}

	public boolean isRespawnPos(BlockPos pos, boolean clear) {
		return false;
	}

	protected Iterable<RestockEntry> getRestockEntries() {
		return this.restockEntries;
	}

	protected StatusEffectInstance[] getStatusEffects() {
		return new StatusEffectInstance[0];
	}

	public final void applyInventory() {
		entry.getPlayer().getInventory().clear();
		if (this.player.currentScreenHandler != null) {
			this.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
		}

		// Add status effects
		for (StatusEffectInstance effect : this.getStatusEffects()) {
			player.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier(), true, false));
		}

		List<ItemStack> armorStacks = this.getArmorStacks();
		int index = 3;
		for (ItemStack stack : armorStacks) {
			player.getInventory().armor.set(index, stack);
			index -= 1;
		}
	
		List<ItemStack> stacks = new ArrayList<>();
		this.appendInitialStacks(stacks);
		int slot = 0;
		for (ItemStack stack : stacks) {
			player.getInventory().setStack(slot, this.phase.isOldCombat() ? OldCombat.applyTo(stack) : stack);
			slot += 1;
		}

		this.entry.updateInventory();
	}

	public final void reinitialize() {
		this.applyInventory();
	}

	public final void initialize() {
		this.reinitialize();
		this.entry.getPlayer().sendMessage(this.getInitialMessage(), false);
	}

	protected void setExperienceBar(float progress) {
		this.player.experienceProgress = MathHelper.clamp(progress, 0, 1);
		this.player.setExperienceLevel(0);
	}

	public ActionResult onUseBlock(Hand hand, BlockHitResult hitResult) {
		return ActionResult.PASS;
	}

	public EventResult afterBlockPlace(BlockPos pos, ItemStack stack, BlockState state) {
		return EventResult.PASS;
	}

	public EventResult onBreakBlock(BlockPos pos) {
		return EventResult.PASS;
	}

	public EventResult onDamaged(PlayerEntry target, DamageSource source, float amount) {
		if (source.isIn(DamageTypeTags.IS_FIRE) && !this.isDamagedByFire()) {
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

	protected static ItemStack unbreakableStack(ItemConvertible item) {
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

	private static void addIfNonNull(RegistryWrapper.WrapperLookup registries, Function<RegistryWrapper.WrapperLookup, ItemStack> supplier, List<ItemStack> stacks) {
		addIfNonNull(() -> supplier.apply(registries), stacks);
	}

	private static void addIfNonNull(Supplier<ItemStack> supplier, List<ItemStack> stacks) {
		ItemStack stack = supplier.get();
		if (stack != null) {
			stacks.add(stack);
		}
	}

	protected static ItemStack createPotionStack(ItemConvertible item, Optional<RegistryEntry<Potion>> maybePotion) {
		return maybePotion
			.map(potion -> PotionContentsComponent.createStack(item.asItem(), potion))
			.orElseGet(() -> new ItemStack(item));
	}
}
