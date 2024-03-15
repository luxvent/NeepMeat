package com.neep.meatweapons.item.meatgun;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SimpleModuleSlot implements ModuleSlot
{
    private final Matrix4f matrix;
    private final Listener listener;
    private MeatgunModule module = MeatgunModule.DEFAULT;

    public SimpleModuleSlot(Listener listener, Matrix4f matrix)
    {
        this.listener = listener;
        this.matrix = new Matrix4f(matrix);
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
        module.setTransform(new Matrix4f(matrix));
        listener.markDirty();
    }

    @Override
    public Matrix4f transform()
    {
        return new Matrix4f(matrix);
    }

    public Matrix4f transformStack()
    {
        return null;
    }

}
