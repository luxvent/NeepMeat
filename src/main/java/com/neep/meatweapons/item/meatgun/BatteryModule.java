package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;

public class BatteryModule extends AbstractMeatgunModule
{
    public BatteryModule(MeatgunComponent.Listener listener)
    {
        super(listener);
    }

    public BatteryModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BATTERY;
    }
}
