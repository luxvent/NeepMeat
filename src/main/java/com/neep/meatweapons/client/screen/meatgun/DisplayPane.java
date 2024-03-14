package com.neep.meatweapons.client.screen.meatgun;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

class DisplayPane extends TinkerTableScreen.PaneWidget
{
    private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
    }
}
