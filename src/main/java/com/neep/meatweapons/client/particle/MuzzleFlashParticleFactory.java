package com.neep.meatweapons.client.particle;

import com.neep.meatweapons.client.renderer.meatgun.MeatgunParticleManager;
import com.neep.meatweapons.particle.MuzzleFlashParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class MuzzleFlashParticleFactory implements ParticleFactory<MuzzleFlashParticleType.MuzzleFlashParticleEffect>
{
    protected final SpriteProvider spriteProvider;
    private final Random random;

    public MuzzleFlashParticleFactory(SpriteProvider spriteProvider)
    {
        this.spriteProvider = spriteProvider;
        this.random = Random.create();
    }

    @Nullable
    @Override
    public Particle createParticle(MuzzleFlashParticleType.MuzzleFlashParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
    {
        PlayerEntity player = world.getPlayerByUuid(parameters.getPlayerUUID());
        MuzzleFlashParticle particle = new MuzzleFlashParticle(world, player, x, y, z, parameters.dx, parameters.dy, parameters.dz, parameters.scale);
        particle.setSprite(spriteProvider);
        return particle;
    }

    public static class MuzzleFlashParticle extends SpriteBillboardParticle implements MeatgunParticle
    {
        private final float scale0;
        private final double x0, y0, z0;
        private final PlayerEntity player;
        private final float fpScale;

        protected MuzzleFlashParticle(ClientWorld clientWorld, @Nullable PlayerEntity player, double d, double e, double f, double dx, double dy, double dz, float fpScale)
        {
            super(clientWorld, d, e, f);
            this.player = player;
            this.maxAge = 1;
            this.scale = 0.2F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
            this.scale0 = scale;
            this.fpScale = fpScale;

            this.x0 = dx;
            this.y0 = dy;
            this.z0 = dz;

            MeatgunParticleManager.add(this);
        }

        @Override
        public void tick()
        {
            super.tick();

        }

        @Override
        public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
        {
            scale = MathHelper.lerp((age + tickDelta) / maxAge, scale0, scale0 * 0.8f);

            if (player != null)
            {
                if (player == MinecraftClient.getInstance().player)
                {
                    return;
//                    this.x = camera.getPos().x + x0;
//                    this.y = camera.getPos().y + y0;
//                    this.z = camera.getPos().z + z0;
                }
                else
                {
                    this.x = player.getEyePos().x + x0;
                    this.y = player.getEyePos().y + y0;
                    this.z = player.getEyePos().z + z0;
                }
                this.prevPosX = x;
                this.prevPosY = y;
                this.prevPosZ = z;
            }

            super.buildGeometry(vertexConsumer, camera, tickDelta);
        }

        @Override
        public ParticleTextureSheet getType()
        {
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public boolean isParticleRemoved()
        {
            return !isAlive();
        }

        @Override
        public void render(MatrixStack matrices, Camera camera, VertexConsumer consumer, int overlay, float tickDelta)
        {
            float f = (float) x0;
            float g = (float) y0;
            float h = (float) z0;

            float i = this.getSize(tickDelta) * fpScale;
            Vector3f[] vector3fs = new Vector3f[]{
                    new Vector3f(-1.0F, -1.0F, 0F),
                    new Vector3f(-1.0F, 1.0F, 0F),
                    new Vector3f(1.0F, 1.0F, 0F),
                    new Vector3f(1.0F, -1.0F, 0F)};

            for (int j = 0; j < 4; ++j)
            {
                Vector3f vector3f = vector3fs[j];
                vector3f.mul(i);
                vector3f.add(f, g, h);

                var v4 = new Vector4f(vector3f, 1);
                Matrix4f mat = new Matrix4f(matrices.peek().getPositionMatrix());
                v4.mul(mat);
                vector3f.set(v4.x, v4.y, v4.z);
            }

            float k = this.getMinU();
            float l = this.getMaxU();
            float m = this.getMinV();
            float n = this.getMaxV();
            int o = this.getBrightness(tickDelta);

            // Vertices have to be in this order, which is different to the order in SpriteBillboardParticle. Don't ask me why.
            consumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
                    .color(this.red, this.green, this.blue, this.alpha)
                    .texture(k, m)
                    .overlay(overlay)
                    .light(o)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
                    .color(this.red, this.green, this.blue, this.alpha)
                    .texture(l, m)
                    .overlay(overlay)
                    .light(o)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
                    .color(this.red, this.green, this.blue, this.alpha)
                    .texture(l, n)
                    .overlay(overlay)
                    .light(o)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
                    .color(this.red, this.green, this.blue, this.alpha)
                    .texture(k, n)
                    .overlay(overlay)
                    .light(o)
                    .normal(0, 1, 0)
                    .next();

        }
    }
}
