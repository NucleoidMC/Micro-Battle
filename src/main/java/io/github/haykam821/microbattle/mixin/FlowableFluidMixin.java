package io.github.haykam821.microbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.haykam821.microbattle.Main;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {
	@Inject(method = "canFlow", at = @At("HEAD"), cancellable = true)
	public void applyFluidFlowGameRule(BlockView blockView, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> ci) {
		if (!(blockView instanceof World)) return;
		World world = (World) blockView;

		ManagedGameSpace gameSpace = ManagedGameSpace.forWorld(world);
		if (gameSpace.testRule(Main.FLUID_FLOW) == RuleResult.DENY) {
			ci.setReturnValue(false);
		}
	}
}