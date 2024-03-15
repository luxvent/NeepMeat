package com.neep.meatlib.api.network;

import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public interface ParamCodec<T>
{
    Class<T> clazz();

    T decode(PacketByteBuf buf);

    void encode(T t, PacketByteBuf buf);

    ParamCodec<Integer> INT = of(int.class, (o, buf) -> buf.writeInt(o), PacketByteBuf::readInt);
    ParamCodec<String> STRING = of(String.class, (o, buf) -> buf.writeString(o), PacketByteBuf::readString);
    ParamCodec<UUID> UUID = of(UUID.class, (o, buf) -> buf.writeUuid(o), PacketByteBuf::readUuid);

    static <T> ParamCodec<T> of(Class<T> clazz, Encoder<T> encoder, Decoder<T> decoder)
    {
        return new ParamCodec<T>()
        {
            @Override
            public Class<T> clazz()
            {
                return clazz;
            }

            @Override
            public T decode(PacketByteBuf buf)
            {
                return decoder.decode(buf);
            }

            @Override
            public void encode(T t, PacketByteBuf buf)
            {
                encoder.encode(t, buf);
            }
        };
    }

    interface Encoder<T>
    {
        void encode(T t, PacketByteBuf buf);
    }

    interface Decoder<T>
    {
        T decode(PacketByteBuf buf);
    }
}
