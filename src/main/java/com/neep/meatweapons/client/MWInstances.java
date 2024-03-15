package com.neep.meatweapons.client;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.renderer.BounceGrenadeInstance;

public class MWInstances
{
    public static void init()
    {
        InstancedRenderRegistry.configure(MeatWeapons.BOUNCE_GRENADE).factory(BounceGrenadeInstance::new).apply();
    }
}
