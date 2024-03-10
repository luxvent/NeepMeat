package com.neep.meatweapons.item.meatgun;

import org.joml.Matrix4f;

import java.util.List;

public class BaseModule extends AbstractMeatgunModule
{
//    private MeatgunModule child = new ChuggerModule();
//    private MeatgunModule child = new TripleCarouselModule();
//    private final MeatgunModule child = new DoubleCarouselModule();
    private final MeatgunModule child = new BosherModule();
//    private final MeatgunModule child = new BatteryModule();
    private final ModuleSlot front = new SimpleModuleSlot(new Matrix4f());

    public BaseModule()
    {
        front.set(child);
    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return List.of(front);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE;
    }
}
