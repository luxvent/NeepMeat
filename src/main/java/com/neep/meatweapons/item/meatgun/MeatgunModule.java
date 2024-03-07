package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.MeatWeapons;
import net.minecraft.util.Identifier;

import java.util.List;

public interface MeatgunModule
{
    List<MeatgunModule> getChildren();

    Type<? extends MeatgunModule> getType();

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
}
