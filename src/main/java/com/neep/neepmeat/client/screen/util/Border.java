package com.neep.neepmeat.client.screen.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import java.util.function.Supplier;

public class Border implements Rectangle, Drawable
{
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final int padding;
    private final Supplier<Integer> col;

    public Border(int x, int y, int w, int h, int padding, Supplier<Integer> col)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.padding = padding;
        this.col = col;
    }

    public Rectangle withoutPadding()
    {
        return new Immutable(x + padding, y + padding, w - padding, h - padding);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.renderBorder(context, x, y, w, h, col.get(), 0);
    }

    @Override
    public int x()
    {
        return x;
    }

    @Override
    public int y()
    {
        return y;
    }

    @Override
    public int w()
    {
        return w;
    }

    @Override
    public int h()
    {
        return h;
    }

    public int padding()
    {
        return padding;
    }
}
