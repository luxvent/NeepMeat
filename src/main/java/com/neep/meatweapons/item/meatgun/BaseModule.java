package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;
import org.joml.Matrix4f;

import java.util.List;

public class BaseModule extends AbstractMeatgunModule
{
//    private MeatgunModule child = new ChuggerModule();
//    private MeatgunModule child = new TripleCarouselModule();
//    private final MeatgunModule child = new DoubleCarouselModule();
//    private final MeatgunModule child = new BosherModule();
//    private final MeatgunModule child = new BatteryModule();
//    private final MeatgunModule child = new LongBoiModule();
    private final ModuleSlot front = new SimpleModuleSlot(new Matrix4f());

    public BaseModule()
    {
        setSlots(List.of(front));
        MeatgunModule child = new BosherModule();
//        MeatgunModule child = new UnderbarrelModule();
        front.set(child);
    }

    public BaseModule(NbtCompound nbt)
    {
        this();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE;
    }

    public static BaseModule fromNbt(NbtCompound nt)
    {
        return new BaseModule(nt);
    }
}
