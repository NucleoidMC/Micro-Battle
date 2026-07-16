package io.github.haykam821.microbattle.game.kit;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import io.github.haykam821.microbattle.PoolHelper;
import io.github.haykam821.microbattle.game.PlayerEntry;
import xyz.nucleoid.plasmid.api.game.common.OldCombat;

public class FoxKit extends Kit {
	private static final WeightedList<DigEntry> DIG_ITEMS = WeightedList.<DigEntry>builder()
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
	protected MobEffectInstance[] getStatusEffects() {
		return new MobEffectInstance[] {
			new MobEffectInstance(MobEffects.SPEED, Integer.MAX_VALUE, 2),
		};
	}

	private boolean canDig() {
		return this.player.isShiftKeyDown() && this.player.onGround();
	}

	private void dig() {
		this.digTicks = RESET_DIG_TICKS;
		Vec3 pos = entry.getPlayer().position();
		entry.getPlayer().connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.GRASS_BREAK), SoundSource.BLOCKS, pos.x(), pos.y(), pos.z(), 1, 1, entry.getPlayer().level().getRandom().nextLong()));

		ItemStack stack = this.getDigStack();
		if (stack != null) {
			entry.getPlayer().addItem(entry.getPhase().isOldCombat() ? OldCombat.applyTo(stack) : stack);
		}
	}

	private ItemStack getDigStack() {
		WeightedList<DigEntry> pool = PoolHelper.filter(DIG_ITEMS, entry -> !entry.isRestricted(this.player));

		Optional<DigEntry> optional = pool.getRandom(entry.getPlayer().getRandom());
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
		return SoundEvents.FOX_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.FOX_HURT;
	}

	protected static ItemStack durabilityStack(ItemLike item, int durability) {
		ItemStack stack = new ItemStack(item);

		stack.setDamageValue(stack.getMaxDamage() - durability);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.COOKED_CHICKEN, 8);
	}

	private record DigEntry(ItemStack stack, boolean unique) {
		private boolean isRestricted(ServerPlayer player) {
			return this.unique && player.getInventory().countItem(stack.getItem()) > 0;
		}
	}
}
