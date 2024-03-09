package com.neep.meatweapons.item.meatgun;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SimpleModuleSlot implements ModuleSlot
{
    private final Matrix4f matrix;
    private MeatgunModule module = MeatgunModule.DEFAULT;

    public SimpleModuleSlot(Matrix4f matrix)
    {
        this.matrix = matrix;
    }

    @Override
    public @NotNull MeatgunModule get()
    {
        return module;
    }

    @Override
    public void set(MeatgunModule module)
    {
        this.module = module;
        module.setTransform(matrix);
    }

    @Override
    public Matrix4f transform()
    {
        return matrix;
    }

    public Matrix4f transformStack()
    {
        return null;
    }
}
