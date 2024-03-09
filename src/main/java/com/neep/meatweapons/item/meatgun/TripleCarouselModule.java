package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.particle.MWGraphicsEffects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TripleCarouselModule implements MeatgunModule
{
    public TripleCarouselModule()
    {

    }

    @Override
    public List<MeatgunModule> getChildren()
    {
        return List.of();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.TRIPLE_CAROUSEL;
    }


    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        boolean sneak = entity.isSneaking();
        return new Vec3d(
                sneak ? 0 : entity.getMainHandStack().equals(stack) ? -0.13 : 0.13,
                -0.04,
                .25);
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, velocity, 0.1f, 1);
        }
    }
}
