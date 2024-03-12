package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;

public interface MeatgunModule
{
    List<ModuleSlot> getChildren();

    Type<? extends MeatgunModule> getType();

    default void tick()
    {
        getChildren().forEach(s -> s.get().tick());
    }

    default void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.get().trigger(world, player, stack, id, pitch, yaw, handType));
    }

    default void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.get().tickTrigger(world, player, stack, id, pitch, yaw, handType));
    }

    void setTransform(Matrix4f transform);

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    static NbtCompound toNbt(MeatgunModule module)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", module.getType().getId().toString());
        module.writeNbt(nbt);
        return nbt;
    }

    static Type<?> readType(NbtCompound nbt)
    {
        Identifier id = new Identifier(nbt.getString("id"));
        return MeatgunModules.REGISTRY.get(id);
    }

    MeatgunModule DEFAULT = new MeatgunModule()
    {
        @Override
        public List<ModuleSlot> getChildren()
        {
            return List.of();
        }

        @Override
        public Type<? extends MeatgunModule> getType()
        {
            return DEFAULT_TYPE;
        }

        @Override
        public void setTransform(Matrix4f transform)
        {

        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }

//        @Override
//        public EnumSet<ChildProperties> getChildProperties()
//        {
//             TODO
//            return null;
//        }
    };

    Type<?> DEFAULT_TYPE = new MeatgunModule.Type<>(new Identifier(MeatWeapons.NAMESPACE, "default"), p -> DEFAULT, n -> DEFAULT);



    @FunctionalInterface
    interface Factory<T extends MeatgunModule>
    {
        T create(MeatgunModule parent);
    }

    @FunctionalInterface
    interface NbtFactory<T extends MeatgunModule>
    {
        T create(NbtCompound nbt);
    }

    class Type<T extends MeatgunModule>
    {
        private final Identifier id;
        private final Factory<T> factory;

        private final NbtFactory<T> decoder;

        public Type(Identifier id, Factory<T> factory, NbtFactory decoder)
        {
            this.id = id;
            this.factory = factory;
            this.decoder = decoder;
        }

        public Identifier getId()
        {
            return id;
        }
        public T create(NbtCompound nbt)
        {
            return decoder.create(nbt);
        }

    }

    enum ChildProperties
    {
        AUXILIARY,


    }

    enum ParentProperties
    {

    }
}
