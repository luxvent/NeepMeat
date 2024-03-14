package com.neep.meatlib.network;

import com.neep.meatlib.api.network.ChannelFormat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerChannelReceiver<T> implements Receiver<T>
{
    private final Identifier name;
    private final ServerPlayNetworkHandler handler;
    private final ChannelFormat<T> format;
    private final T listener;

    public ServerChannelReceiver(ServerPlayerEntity player, Identifier name, ChannelFormat<T> format, T listener)
    {
        this.name = name;
        this.handler = player.networkHandler;
        this.format = format;
        this.listener = listener;
        ServerPlayNetworking.registerReceiver(handler, name, (server, player1, handler1, buf, responseSender) ->
                receive(buf));
    }

    public void receive(PacketByteBuf buf)
    {
        format.receive(listener, buf);
    }

    @Override
    public void close()
    {
        ServerPlayNetworking.unregisterReceiver(handler, name);
    }
}
