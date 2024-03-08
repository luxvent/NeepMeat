package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.client.particle.MeatgunParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MeatgunParticleManager
{
    private static final List<MeatgunParticle> PARTICLES = new ArrayList<>();

    public static void add(MeatgunParticle particle)
    {
        PARTICLES.add(particle);
    }

    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client -> PARTICLES.removeIf(MeatgunParticle::isParticleRemoved));
    }

    public static Iterable<MeatgunParticle> getParticles()
    {
        return PARTICLES;
    }
}
