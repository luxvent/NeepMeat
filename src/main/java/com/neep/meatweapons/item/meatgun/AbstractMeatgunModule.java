package com.neep.meatweapons.item.meatgun;

import org.joml.Matrix4f;

public abstract class AbstractMeatgunModule implements MeatgunModule
{
    protected Matrix4f transform = new Matrix4f();

    @Override
    public void setTransform(Matrix4f transform)
    {
        this.transform = transform;
    }
}
