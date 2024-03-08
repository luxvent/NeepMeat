package com.neep.meatweapons.client.particle;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface MeatgunParticle
{
    boolean isParticleRemoved();

    void render(MatrixStack matrices, Camera camera, VertexConsumer vcp, int overlay, float tickDelta);
}
