package com.neep.meatlib.network.screen;

import com.neep.meatlib.api.network.ParamCodec;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class ScreenChannel<T>
{
    private final Identifier name;
    private final ParamCodec<T> codec;
    private List<ServerHandler> serverHandlers;

    public static Sender server(ServerPlayerEntity player)
    {
        return (channel, buf) -> ServerPlayNetworking.send(player, channel, buf);
    }

    public ScreenChannel(Identifier name, ParamCodec<T> codec)
    {
        this.name = name;
        this.codec = codec;
    }

    public void send(T t, Sender sender)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        codec.encode(t, buf);
        sender.send(name, buf);
    }

    public void attach(Consumer<T> consumer, ServerPlayerEntity player)
    {
        serverHandlers.add(new ServerHandler(player, consumer));
    }

    public void close()
    {
        for (var handler : serverHandlers)
        {
            handler.close();
        }
    }

    private class ServerHandler
    {
        private final ServerPlayerEntity player;
        private final Consumer<T> consumer;

        public ServerHandler(ServerPlayerEntity player, Consumer<T> consumer)
        {
            this.player = player;
            this.consumer = consumer;
            ServerPlayNetworking.registerReceiver(player.networkHandler, name, (server, player1, handler, buf, responseSender) ->
            {
                T t = codec.decode(buf);
                server.execute(() -> this.consumer.accept(t));
            });
        }

        public void close()
        {
            ServerPlayNetworking.unregisterReceiver(player.networkHandler, name);
        }
    }

    private interface Sender
    {
        void send(Identifier channel, PacketByteBuf buf);
    }
}
