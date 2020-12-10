package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import io.github.haykam821.microbattle.game.PlayerEntry;
import io.github.haykam821.microbattle.game.phase.MicroBattleActivePhase;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.plasmid.logic.combat.OldCombat;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public abstract class Kit {
	protected static final Random RANDOM = new Random();

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
		MutableText text = new LiteralText(linePrefix);
		text.append(new LiteralText("• Defeat the other players!").formatted(Formatting.GRAY));

		for (String line : this.getNeutrals()) {
			text.append(new LiteralText("\n" + linePrefix + "• " + line).formatted(Formatting.GRAY));
		}
		for (String line : this.getAdvantages()) {
			text.append(new LiteralText("\n" + linePrefix + "+ " + line).formatted(Formatting.GREEN));
		}
		for (String line : this.getDisadvantages()) {
			text.append(new LiteralText("\n" + linePrefix + "- " + line).formatted(Formatting.RED));
		}

		return text;
	}

	private Text getName() {
		return this.type.getName();
	}

	private Text getHoverableName() {
		return this.getName().shallowCopy().styled(style -> {
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.getTooltip("")));
		});
	}

	public MutableText getReceivedMessage() {
		if (this.entry.getTeam() == null) {
			return new TranslatableText("text.microbattle.kit_received", this.getHoverableName()).formatted(Formatting.GRAY);
		} else {
			Text teamName = new LiteralText(this.entry.getTeam().getDisplay()).formatted(this.entry.getTeam().getFormatting());
			return new TranslatableText("text.microbattle.team_kit_received", this.getHoverableName(), teamName).formatted(Formatting.GRAY);
		}
	}

	public Text getInitialMessage() {
		return this.getReceivedMessage().append("\n").append(this.getTooltip("  "));
	}

	protected ItemStack createArmorStack(Item item, String type, boolean secondary) {
		return ItemStackBuilder.of(item)
			.addEnchantment(Enchantments.BINDING_CURSE, 1)
			.setColor(secondary ? this.getSecondaryColor() : this.getBaseColor())
			.setName(new TranslatableText("text.microbattle.team_armor." + type, this.getName()))
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

	public void tick() {
		for (RestockEntry entry : this.restockEntries) {
			entry.tick(this.entry);
		}
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
		return new ItemStack(Items.APPLE, 8);
	}

	protected void appendInitialStacks(List<ItemStack> stacks) {
		addIfNonNull(this::getMainWeaponStack, stacks);
		addIfNonNull(this::getPickaxeToolStack, stacks);
		addIfNonNull(this::getAxeToolStack, stacks);
		addIfNonNull(this::getShovelToolStack, stacks);
		addIfNonNull(this::getFoodStack, stacks);
		
		for (RestockEntry entry : this.restockEntries) {
			addIfNonNull(entry::supplyStack, stacks);
		}
	}

	protected StatusEffectInstance[] getStatusEffects() {
		return new StatusEffectInstance[0];
	}

	public final void applyInventory() {
		entry.getPlayer().inventory.clear();

		// Add status effects
		for (StatusEffectInstance effect : this.getStatusEffects()) {
			player.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier(), true, false));
		}

		List<ItemStack> armorStacks = this.getArmorStacks();
		int index = 3;
		for (ItemStack stack : armorStacks) {
			player.inventory.armor.set(index, stack);
			index -= 1;
		}
	
		List<ItemStack> stacks = new ArrayList<>();
		this.appendInitialStacks(stacks);
		int slot = 0;
		for (ItemStack stack : stacks) {
			player.inventory.setStack(slot, this.phase.isOldCombat() ? OldCombat.applyTo(stack) : stack);
			slot += 1;
		}

		// Update inventory
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}

	public final void initialize() {
		this.applyInventory();
		this.entry.getPlayer().sendMessage(this.getInitialMessage(), false);
	}

	protected void setExperienceBar(float progress) {
		this.player.experienceProgress = MathHelper.clamp(progress, 0, 1);
		this.player.setExperienceLevel(0);
	}

	public ActionResult onUseBlock(Hand hand, BlockHitResult hitResult) {
		return ActionResult.PASS;
	}

	public ActionResult onDamaged(PlayerEntry target, DamageSource source, float amount) {
		return ActionResult.PASS;
	}

	public ActionResult onDealDamage(PlayerEntry target, DamageSource source, float amount) {
		return ActionResult.PASS;
	}

	protected static ItemStack unbreakableStack(ItemConvertible item) {
		return ItemStackBuilder.of(item).setUnbreakable().build();
	}

	private static void addIfNonNull(Supplier<ItemStack> supplier, List<ItemStack> stacks) {
		ItemStack stack = supplier.get();
		if (stack != null) {
			stacks.add(stack);
		}
	}

	protected static ItemStack potionLikeStack(ItemConvertible item, Potion potion) {
		ItemStack stack = new ItemStack(item);
		stack.getOrCreateTag().putString("Potion", Registry.POTION.getId(potion).toString());
		return stack;
	}
}
