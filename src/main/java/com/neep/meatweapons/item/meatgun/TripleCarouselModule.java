package com.neep.meatweapons.item.meatgun;

import java.util.List;

public class TripleCarouselModule extends AbstractMeatgunModule
{
    public TripleCarouselModule()
    {

    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return List.of();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.TRIPLE_CAROUSEL;
    }
}
