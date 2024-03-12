package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;

public class TripleCarouselModule extends AbstractMeatgunModule
{
    public TripleCarouselModule()
    {

    }

    public TripleCarouselModule(NbtCompound nbt)
    {

    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.TRIPLE_CAROUSEL;
    }
}
