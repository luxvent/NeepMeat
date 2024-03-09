package com.neep.meatweapons.item.meatgun;

import java.util.List;

public class BaseModule implements MeatgunModule
{
//    private MeatgunModule child = new ChuggerModule();
    private MeatgunModule child = new TripleCarouselModule();

    public BaseModule()
    {

    }

    @Override
    public List<MeatgunModule> getChildren()
    {
        return List.of(child);
    }


    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE;
    }
}
