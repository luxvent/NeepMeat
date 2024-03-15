package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class BatteryModule extends AbstractMeatgunModule
{
    public BatteryModule(ModuleSlot.Listener listener)
    {
        super(listener);
    }

    public BatteryModule(ModuleSlot.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BATTERY;
    }
}
