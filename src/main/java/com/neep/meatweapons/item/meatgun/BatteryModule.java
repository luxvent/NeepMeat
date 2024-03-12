package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class BatteryModule extends AbstractMeatgunModule
{
    public BatteryModule()
    {
    }

    public BatteryModule(NbtCompound nbt)
    {
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BATTERY;
    }
}
