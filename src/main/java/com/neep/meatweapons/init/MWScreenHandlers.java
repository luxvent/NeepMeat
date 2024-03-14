package com.neep.meatweapons.init;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.screen.TinkerTableScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class MWScreenHandlers
{
    public static ScreenHandlerType<TinkerTableScreenHandler> MEATGUN = new ScreenHandlerType<>(TinkerTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static void init()
    {
        MEATGUN = Registry.register(Registries.SCREEN_HANDLER, new Identifier(MeatWeapons.NAMESPACE, "meatgun"), MEATGUN);
    }
}
