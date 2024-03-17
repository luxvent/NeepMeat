package com.neep.meatweapons.item.meatgun;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public interface ModuleSlot
{
    @NotNull MeatgunModule get();

    void set(MeatgunModule module);

    Matrix4f transform();

    Matrix4f transform(float tickDelta);
}
