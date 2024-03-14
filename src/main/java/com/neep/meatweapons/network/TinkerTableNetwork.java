package com.neep.meatweapons.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TinkerTableNetwork
{
    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void sendSlotClick()
        {

        }
    }
}
