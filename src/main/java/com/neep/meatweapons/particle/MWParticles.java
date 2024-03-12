package com.neep.meatweapons.particle;

import com.neep.meatlib.registry.ParticleRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.particle.MuzzleFlashParticleFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;

public class MWParticles
{
    public static DefaultParticleType PLASMA_PARTICLE = FabricParticleTypes.simple();
    public static ParticleType<MuzzleFlashParticleType.MuzzleFlashParticleEffect> NORMAL_MUZZLE_FLASH = new MuzzleFlashParticleType(true,
            MuzzleFlashParticleType.createCodec(MWParticles.NORMAL_MUZZLE_FLASH));
    public static ParticleType<MuzzleFlashParticleType.MuzzleFlashParticleEffect> LONG_BOI_MUZZLE_FLASH = new MuzzleFlashParticleType(true,
            MuzzleFlashParticleType.createCodec(MWParticles.LONG_BOI_MUZZLE_FLASH));

    public static void init()
    {
        PLASMA_PARTICLE = ParticleRegistry.register(MeatWeapons.NAMESPACE, "plasma", PLASMA_PARTICLE);
        NORMAL_MUZZLE_FLASH = ParticleRegistry.register(MeatWeapons.NAMESPACE, "normal_muzzle_flash", NORMAL_MUZZLE_FLASH);
        LONG_BOI_MUZZLE_FLASH = ParticleRegistry.register(MeatWeapons.NAMESPACE, "long_boi_muzzle_flash", LONG_BOI_MUZZLE_FLASH);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient()
    {
//        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) ->
//        {
//            registry.register(new Identifier(MeatWeapons.NAMESPACE, "particle/plasma"));
//        }));

        ParticleFactoryRegistry.getInstance().register(PLASMA_PARTICLE, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(NORMAL_MUZZLE_FLASH, MuzzleFlashParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(LONG_BOI_MUZZLE_FLASH, MuzzleFlashParticleFactory::new);
    }

}
