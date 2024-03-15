package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;

public class TripleCarouselModule extends AbstractMeatgunModule
{
    public TripleCarouselModule(ModuleSlot.Listener listener)
    {
        super(listener);
    }

    public TripleCarouselModule(ModuleSlot.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.TRIPLE_CAROUSEL;
    }
}
