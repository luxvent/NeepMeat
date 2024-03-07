package com.neep.meatweapons.item.meatgun;

import java.util.List;

public class BaseModule implements MeatgunModule
{
    private MeatgunModule child = MeatgunModule.DEFAULT;

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
