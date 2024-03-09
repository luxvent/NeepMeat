package com.neep.meatweapons.item.meatgun;

import java.util.List;

public class BatteryModule extends AbstractMeatgunModule
{
    public BatteryModule()
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
        return MeatgunModules.BATTERY;
    }
}
