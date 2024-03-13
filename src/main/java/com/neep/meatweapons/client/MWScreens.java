package com.neep.meatweapons.client;

import com.neep.meatweapons.client.screen.meatgun.MeatgunScreen;
import com.neep.meatweapons.init.MWScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(value= EnvType.CLIENT)
public class MWScreens
{
    public static void init()
    {
        HandledScreens.register(MWScreenHandlers.MEATGUN, MeatgunScreen::new);
    }
}
