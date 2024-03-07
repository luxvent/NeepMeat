package com.neep.meatweapons.item;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public
interface BeamEffectProvider
{
    void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius);
}
