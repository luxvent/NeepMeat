package com.neep.meatweapons.client.screen.meatgun;

import com.neep.meatlib.client.ClientChannelSender;
import com.neep.meatlib.network.Sender;
import com.neep.meatweapons.screen.TinkerTableScreenHandler;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.BorderSlot;
import com.neep.neepmeat.client.screen.util.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TinkerTableScreen extends HandledScreen<TinkerTableScreenHandler>
{
    private final DisplayPane displayPane = new DisplayPane();
    private final TreePane treePane;
    private final Sender<TinkerTableScreenHandler.SlotClick> sender;

    public TinkerTableScreen(TinkerTableScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        treePane = new TreePane(handler, handler.getSlot(0));

        this.sender = new ClientChannelSender<>(TinkerTableScreenHandler.CHANNEL_ID, TinkerTableScreenHandler.CHANNEL_FORMAT);
    }

    @Override
    protected void init()
    {
        backgroundWidth = TinkerTableScreenHandler.BACKGROUND_WIDTH;
        backgroundHeight = TinkerTableScreenHandler.BACKGROUND_HEIGHT;
        super.init();

        Border border = new Border(x, y, backgroundWidth, backgroundHeight, 3, () -> PLCCols.BORDER.col);
        Rectangle withoutPadding = border.withoutPadding();
        Rectangle bounds = new Rectangle.Immutable(withoutPadding.x(), withoutPadding.y(), withoutPadding.w(), withoutPadding.h() - 19);
        addDrawable(border);

        // Hotbar and slot
        addDrawable(new BorderSlot(bounds.x(), withoutPadding.y() + withoutPadding.h() - 17, () -> PLCCols.BORDER.col));
        addDrawable(new Border(bounds.x() + 18 + 1, withoutPadding.y() + withoutPadding.h() - 17, 18 * 9 - 1, 17, 0, () -> PLCCols.BORDER.col));
        var inventoryBorder = new Border(bounds.x() + 18 + 1, withoutPadding.y() + withoutPadding.h() - 72, 18 * 9 - 1, 3 * 17 + 2, 0, () -> PLCCols.BORDER.col);
        addDrawable(inventoryBorder);

        Rectangle.Mutable displayPaneBounds = new Rectangle.Mutable(bounds)
                .setH(inventoryBorder.y() - bounds.y() - 2)
                .setW(18 * 9 + 17 + 1);
        displayPane.init(displayPaneBounds);
        addDrawableChild(displayPane);

        Rectangle.Mutable treePaneBounds = new Rectangle.Mutable(withoutPadding)
                .setX(displayPaneBounds.x() + displayPaneBounds.w() + 2)
                .setW(bounds.w() - displayPaneBounds.w() - border.padding() + 1);
        treePane.init(treePaneBounds);
        addDrawableChild(treePane);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (treePane.isMouseOver(mouseX, mouseY))
        {
            return treePane.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected void handledScreenTick()
    {
        treePane.tick();
    }

    static abstract class PaneWidget implements Drawable, Element, Selectable
    {
        protected boolean focused;
        protected Rectangle bounds;
        protected Border border;

        public void init(Rectangle parentSize)
        {
            this.bounds = new Rectangle.Immutable(parentSize);
            this.border = new Border(bounds, 0, () -> PLCCols.BORDER.col);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            border.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY)
        {
            return bounds.isWithin(mouseX, mouseY);
        }

        @Override
        public void setFocused(boolean focused)
        {
            this.focused = focused;
        }

        @Override
        public boolean isFocused()
        {
            return focused;
        }

        @Override
        public SelectionType getType()
        {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }
    }
}
