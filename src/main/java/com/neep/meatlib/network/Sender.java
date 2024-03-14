package com.neep.meatlib.network;

import net.minecraft.network.PacketByteBuf;

public interface Sender<T>
{
    T emitter();

    void send(PacketByteBuf buf);
}
