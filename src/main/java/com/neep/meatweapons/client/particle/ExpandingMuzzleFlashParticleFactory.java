package com.neep.meatweapons.client.particle;

import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ExpandingMuzzleFlashParticleFactory implements ParticleFactory<MuzzleFlashParticleType.MuzzleFlashParticleEffect>
{
    protected final SpriteProvider spriteProvider;
    private final Random random;

    public ExpandingMuzzleFlashParticleFactory(SpriteProvider spriteProvider)
    {
        this.spriteProvider = spriteProvider;
        this.random = Random.create();
    }

    @Nullable
    @Override
    public Particle createParticle(MuzzleFlashParticleType.MuzzleFlashParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
    {
        PlayerEntity player = world.getPlayerByUuid(parameters.getPlayerUUID());
        ExpandingMuzzleFlashParticle particle = new ExpandingMuzzleFlashParticle(world, player, x, y, z, parameters.dx, parameters.dy, parameters.dz, parameters.scale, parameters.maxAge);
        particle.setSprite(spriteProvider);
        return particle;
    }

    public static class ExpandingMuzzleFlashParticle extends MuzzleFlashParticleFactory.MuzzleFlashParticle
    {
        protected ExpandingMuzzleFlashParticle(ClientWorld clientWorld, @Nullable PlayerEntity player, double d, double e, double f, double dx, double dy, double dz, float fpScale, int maxAge)
        {
            super(clientWorld, player, d, e, f, dx, dy, dz, fpScale, maxAge);
        }

        @Override
        public float getSize(float tickDelta)
        {
            return MathHelper.lerp((age + tickDelta) / maxAge, scale0, scale0 * 1.3f);
        }
    }
}
