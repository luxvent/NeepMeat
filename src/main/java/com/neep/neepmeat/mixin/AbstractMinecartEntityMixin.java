package com.neep.neepmeat.mixin;

import com.neep.neepmeat.interfaces.AbstractMinecartEntityAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin implements AbstractMinecartEntityAccess
{
    @Unique
    private Vec3d controllerVelocity = Vec3d.ZERO;

    // Passenger velocity is reset at some point after this is called.
    @Inject(method = "moveOnRail", at = @At("HEAD"))
    void onMoveOnRail(BlockPos pos, BlockState state, CallbackInfo ci)
    {
        if (((Entity) (Object) (this)).getFirstPassenger() instanceof PlayerEntity player)
        {
            controllerVelocity = player.getVelocity();
        }
    }

    @Override
    public Vec3d neepmeat$getControllerVelocity()
    {
        return controllerVelocity;
    }
}
