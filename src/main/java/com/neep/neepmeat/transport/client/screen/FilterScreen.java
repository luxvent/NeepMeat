package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class FilterScreen extends BaseHandledScreen<FilterScreenHandler>
{
    public FilterScreen(FilterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);

        handler.channel.receiver(handler::test);
    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 100;
        this.backgroundHeight = 100;
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

    @Override
    public void close()
    {
        super.close();
    }
}
