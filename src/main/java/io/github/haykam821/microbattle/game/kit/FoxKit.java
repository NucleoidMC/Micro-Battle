package io.github.haykam821.microbattle.game.kit;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.WeightedList;
import xyz.nucleoid.plasmid.logic.combat.OldCombat;

public class FoxKit extends Kit {
	private static final WeightedList<ItemStack> DIG_ITEMS = new WeightedList<>();

	public FoxKit() {
		super(DyeColor.WHITE.getFireworkColor(), DyeColor.ORANGE.getFireworkColor());
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"You can dig up items by sneaking on the ground",
			"You are swifter than usual",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You do not have any tools by default",
		};
	}

	@Override
	protected ItemStack getMainWeaponStack() {
		return null;
	}

	@Override
	protected ItemStack getPickaxeToolStack() {
		return null;
	}

	@Override
	protected ItemStack getAxeToolStack() {
		return null;
	}

	@Override
	protected ItemStack getShovelToolStack() {
		return null;
	}

	@Override
	protected StatusEffectInstance[] getStatusEffects() {
		return new StatusEffectInstance[] {
			new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2),
		};
	}

	@Override
	public void tick(PlayerEntry entry) {
		if (entry.getTicks() % (20 * 10) != 0) return;
		if (!entry.getPlayer().isSneaking()) return;
		if (!entry.getPlayer().isOnGround()) return;

		entry.getPlayer().playSound(SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1, 1);

		ItemStack stack = DIG_ITEMS.pickRandom(entry.getPlayer().getRandom()).copy();
		entry.getPlayer().giveItemStack(entry.getPhase().isOldCombat() ? OldCombat.applyTo(stack) : stack);
	}

	protected static ItemStack durabilityStack(ItemConvertible item, int durability) {
		ItemStack stack = new ItemStack(item);

		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt("Damage", stack.getMaxDamage() - durability);

		return stack;
	}

	static {
		DIG_ITEMS.add(durabilityStack(Items.IRON_SWORD, 4), 500);
		DIG_ITEMS.add(durabilityStack(Items.IRON_PICKAXE, 32), 500);
		DIG_ITEMS.add(durabilityStack(Items.IRON_AXE, 4), 500);
		DIG_ITEMS.add(durabilityStack(Items.IRON_SHOVEL, 32), 500);
		DIG_ITEMS.add(new ItemStack(Items.EGG, 4), 500);
		DIG_ITEMS.add(new ItemStack(Items.TOTEM_OF_UNDYING), 1);
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.COOKED_CHICKEN, 8);
	}
}
