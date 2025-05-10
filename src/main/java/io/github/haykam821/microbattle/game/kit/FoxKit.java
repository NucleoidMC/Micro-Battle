package io.github.haykam821.microbattle.game.kit;

import java.util.Optional;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import xyz.nucleoid.plasmid.api.game.common.OldCombat;

public class FoxKit extends Kit {
	private static final DataPool<DigEntry> DIG_ITEMS = DataPool.<DigEntry>builder()
		.add(new DigEntry(durabilityStack(Items.IRON_SWORD, 4), true), 500)
		.add(new DigEntry(durabilityStack(Items.IRON_PICKAXE, 32), true), 500)
		.add(new DigEntry(durabilityStack(Items.IRON_AXE, 4), true), 500)
		.add(new DigEntry(durabilityStack(Items.IRON_SHOVEL, 32), true), 500)
		.add(new DigEntry(new ItemStack(Items.EGG, 4), false), 500)
		.add(new DigEntry(new ItemStack(Items.TOTEM_OF_UNDYING), false), 1)
		.build();

	private static final int IDLE_DIG_TICKS = 20 * 1;
	private static final int RESET_DIG_TICKS = 20 * 3;

	private int digTicks = IDLE_DIG_TICKS;

	public FoxKit(PlayerEntry entry) {
		super(KitTypes.FOX, entry);
	}
	
	@Override
	protected int getBaseColor() {
		return DyeColor.WHITE.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.ORANGE.getFireworkColor();
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

	private boolean canDig() {
		return this.player.isSneaking() && this.player.isOnGround();
	}

	private void dig() {
		this.digTicks = RESET_DIG_TICKS;
		entry.getPlayer().playSoundToPlayer(SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1, 1);

		ItemStack stack = this.getDigStack();
		if (stack != null) {
			entry.getPlayer().giveItemStack(entry.getPhase().isOldCombat() ? OldCombat.applyTo(stack) : stack);
		}
	}

	private ItemStack getDigStack() {
		DataPool.Builder<DigEntry> builder = DataPool.builder();

		for (Weighted.Present<DigEntry> entry : DIG_ITEMS.getEntries()) {
			if (!entry.data().isRestricted(this.player)) {
				builder.add(entry.data(), entry.getWeight().getValue());
			}
		}

		Optional<DigEntry> optional = builder.build().getDataOrEmpty(entry.getPlayer().getRandom());
		return optional.isPresent() ? optional.get().stack().copy() : null;
	}

	@Override
	public void tick() {
		super.tick();
		this.setExperienceBar((IDLE_DIG_TICKS - this.digTicks) / (float) IDLE_DIG_TICKS);

		if (this.canDig() || this.digTicks > IDLE_DIG_TICKS) {
			this.digTicks -= 1;
			if (this.digTicks <= 0) {
				this.dig();
			}
		} else if (this.digTicks < IDLE_DIG_TICKS) {
			this.digTicks += 1;
		}

	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_FOX_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_FOX_HURT;
	}

	protected static ItemStack durabilityStack(ItemConvertible item, int durability) {
		ItemStack stack = new ItemStack(item);

		stack.setDamage(stack.getMaxDamage() - durability);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.COOKED_CHICKEN, 8);
	}

	private record DigEntry(ItemStack stack, boolean unique) {
		private boolean isRestricted(ServerPlayerEntity player) {
			return this.unique && player.getInventory().count(stack.getItem()) > 0;
		}
	}
}
