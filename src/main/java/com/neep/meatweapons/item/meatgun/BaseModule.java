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

    private final ModuleSlot front;

    public BaseModule(ModuleSlot.Listener listener)
    {
        super(listener);
        front = new SimpleModuleSlot(this.listener, new Matrix4f());

        setSlots(List.of(front));
        MeatgunModule child = new BosherModule(listener);
        front.set(child);
    }

    public BaseModule(ModuleSlot.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BASE;
    }

    public static BaseModule fromNbt(ModuleSlot.Listener listener, NbtCompound nt)
    {
        return new BaseModule(listener, nt);
    }
}
