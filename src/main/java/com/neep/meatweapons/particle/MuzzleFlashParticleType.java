package com.neep.meatweapons.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

import java.util.UUID;

public class MuzzleFlashParticleType extends ParticleType<MuzzleFlashParticleType.MuzzleFlashParticleEffect>
{
    public static final Codec<UUID> UUID_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("msb").forGetter(UUID::getMostSignificantBits),
                    Codec.LONG.fieldOf("lsb").forGetter(UUID::getLeastSignificantBits)
            ).apply(instance, UUID::new));

    public static Codec<MuzzleFlashParticleEffect> createCodec(ParticleType<MuzzleFlashParticleEffect> type)
    {
        return RecordCodecBuilder.create(
                instance -> instance.group(
                                UUID_CODEC.fieldOf("uuid").forGetter(e -> e.playerUUID),
                                Codec.DOUBLE.fieldOf("dx").forGetter(e -> e.dx),
                                Codec.DOUBLE.fieldOf("dy").forGetter(e -> e.dy),
                                Codec.DOUBLE.fieldOf("dz").forGetter(e -> e.dz),
                                Codec.FLOAT.fieldOf("scale").forGetter(e -> e.scale))
                        .apply(instance, (uuid, aDouble, aDouble2, aDouble3, aFloat) ->
                                new MuzzleFlashParticleEffect(type, uuid, aDouble, aDouble2, aDouble3, aFloat))
        );
    }

    private final Codec<MuzzleFlashParticleEffect> codec;

    public static final ParticleEffect.Factory<MuzzleFlashParticleEffect> PARAMETER_FACTORY = new ParticleEffect.Factory<>()
    {
        @Override
        public MuzzleFlashParticleEffect read(ParticleType<MuzzleFlashParticleEffect> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            String s = reader.readString();
            UUID uuid = UUID.fromString(s);
            double dx = 0;
            double dy = 0;
            double dz = 0;
            float scale = 1;
            return new MuzzleFlashParticleEffect(MWParticles.LONG_BOI_MUZZLE_FLASH, uuid, dx, dy, dz, scale);
        }

        @Override
        public MuzzleFlashParticleEffect read(ParticleType<MuzzleFlashParticleEffect> type, PacketByteBuf buf)
        {
            ParticleType<MuzzleFlashParticleEffect> type1 = (ParticleType<MuzzleFlashParticleEffect>) buf.readRegistryValue(Registries.PARTICLE_TYPE);
            return new MuzzleFlashParticleEffect(type1, buf.readUuid(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
        }
    };

    protected MuzzleFlashParticleType(boolean alwaysShow, Codec<MuzzleFlashParticleEffect> codec)
    {
        super(alwaysShow, PARAMETER_FACTORY);
        this.codec = codec;
    }

    @Override
    public Codec<MuzzleFlashParticleEffect> getCodec()
    {
        return codec;
    }

    public static class MuzzleFlashParticleEffect implements ParticleEffect
    {
        private final ParticleType<MuzzleFlashParticleEffect> type;
        private final UUID playerUUID;
        public final double dx, dy, dz;
        public final float scale;

        public MuzzleFlashParticleEffect(ParticleType<MuzzleFlashParticleEffect> type, PlayerEntity player, double dx, double dy, double dz, float scale)
        {
            this(type, player.getUuid(), dx, dy, dz, scale);
        }

        public MuzzleFlashParticleEffect(ParticleType<MuzzleFlashParticleEffect> type, UUID uuid, double dx, double dy, double dz, float scale)
        {
            this.type = type;
            this.playerUUID = uuid;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.scale = scale;
        }

        @Override
        public ParticleType<MuzzleFlashParticleEffect> getType()
        {
            return type;
        }

        @Override
        public void write(PacketByteBuf buf)
        {
            buf.writeUuid(playerUUID);
            buf.writeDouble(dx);
            buf.writeDouble(dy);
            buf.writeDouble(dz);
            buf.writeFloat(scale);
        }

        public UUID getPlayerUUID()
        {
            return playerUUID;
        }

        @Override
        public String asString()
        {
            return Registries.PARTICLE_TYPE.getId(getType()).toString();
        }
    }
}
