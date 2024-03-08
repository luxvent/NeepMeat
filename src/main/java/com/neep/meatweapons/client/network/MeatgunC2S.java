package com.neep.meatweapons.client.network;

import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.network.MeatgunS2C;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class MeatgunC2S
{
    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(MeatgunS2C.CHANNEL, (client, handler, buf, responseSender) ->
        {
            MeatgunS2C.RecoilDirection direction = MeatgunS2C.RecoilDirection.values()[buf.readInt()];
            float amount = buf.readFloat();
            float horAmount = buf.readFloat();
            float returnSpeed = buf.readFloat();
            float horReturnSpeed = buf.readFloat();

            client.execute(() ->
            {
                MeatgunComponent component = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
                if (component != null)
                {
                    component.getRecoil().set(direction, amount, horAmount, returnSpeed, horReturnSpeed);
                }
//                MeatgunRenderer.INSTANCE.setRecoil(direction, amount, returnSpeed);
            });
        });
    }
}
