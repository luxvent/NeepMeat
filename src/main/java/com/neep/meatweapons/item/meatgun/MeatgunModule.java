package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public interface MeatgunModule
{
    List<MeatgunModule> getChildren();

    Type<? extends MeatgunModule> getType();

    default void tick()
    {
        getChildren().forEach(MeatgunModule::tick);
    }

    default void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.trigger(world, player, stack, id, pitch, yaw, handType));
    }

//    EnumSet<ChildProperties> getChildProperties();

    MeatgunModule DEFAULT = new MeatgunModule()
    {
        @Override
        public List<MeatgunModule> getChildren()
        {
            return List.of();
        }

        @Override
        public Type<? extends MeatgunModule> getType()
        {
            return DEFAULT_TYPE;
        }

//        @Override
//        public EnumSet<ChildProperties> getChildProperties()
//        {
//             TODO
//            return null;
//        }
    };

    Type<?> DEFAULT_TYPE = new MeatgunModule.Type<>(new Identifier(MeatWeapons.NAMESPACE, "default"), p -> DEFAULT);


    @FunctionalInterface
    interface Factory<T extends MeatgunModule>
    {
        T create(MeatgunModule parent);
    }

    class Type<T extends MeatgunModule>
    {
        private final Identifier id;
        private final Factory<T> factory;

        public Type(Identifier id, Factory<T> factory)
        {
            this.id = id;
            this.factory = factory;
        }

        public Identifier getId()
        {
            return id;
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
