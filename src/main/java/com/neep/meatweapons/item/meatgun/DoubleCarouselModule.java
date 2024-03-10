package com.neep.meatweapons.item.meatgun;

import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class DoubleCarouselModule extends AbstractMeatgunModule
{
    private final ModuleSlot upSlot = new SimpleModuleSlot(new Matrix4f().translate(0, 4 / 16f, -3 / 16f));
    private final ModuleSlot downSlot = new SimpleModuleSlot(new Matrix4f().rotateZ(MathHelper.PI).translate(0, 4 / 16f, -3 / 16f));
    private final ModuleSlot auxSlot = new SimpleModuleSlot(new Matrix4f().rotateY(MathHelper.PI).translate(0, 5 / 16f, 0 / 16f));
    private final List<ModuleSlot> slots = new ArrayList<>();

    public DoubleCarouselModule()
    {
        upSlot.set(new ChuggerModule());
        downSlot.set(new BosherModule());
        auxSlot.set(new BatteryModule());
        slots.add(upSlot);
        slots.add(downSlot);
        slots.add(auxSlot);
    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return slots;
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.DOUBLE_CAROUSEL;
    }

}
