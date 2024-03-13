package com.neep.meatweapons.client.screen.meatgun;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

class TreePane extends MeatgunScreen.PaneWidget
{
    private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
    }

    private class ModuleWidget implements Drawable
    {
        private Rectangle.Mutable bounds;
        private Border border;

        public void init()
        {

        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
        }
    }
}
