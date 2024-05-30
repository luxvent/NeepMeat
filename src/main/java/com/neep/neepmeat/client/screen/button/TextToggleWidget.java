package com.neep.neepmeat.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;

import static com.neep.neepmeat.client.screen.button.NMButtonWidget.NM_WIDGETS_TEXTURE;

public class TextToggleWidget extends ClickableWidget implements GUIUtil
{
    protected boolean toggled;
    protected ToggleAction onToggle;

    public TextToggleWidget(int x, int y, int width, int height, Text message, boolean toggled, ToggleAction onToggle)
    {
        super(x, y, width, height, message);
        this.toggled = toggled;
        this.onToggle = onToggle;
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.toggled = !this.toggled;

        this.onToggle.onToggle(this, toggled);
    }

    public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
    {
//        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
    {
        this.renderMain(context, mouseX, mouseY, delta);
        if (this.isHovered())
        {
            this.renderTooltip(context, mouseX, mouseY);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder)
    {

    }

    protected void renderMain(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(toggled);
//        int i = 0; // TODO
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int borderCol = isHovered() || toggled ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
        int textCol = isHovered() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;

        matrices.drawTexture(NM_WIDGETS_TEXTURE, getX(), getY(), 0, 90, this.width / 2, this.height);
        matrices.drawTexture(NM_WIDGETS_TEXTURE, getX() + this.width / 2, getY(), 200 - this.width / 2, 90, this.width / 2, this.height);

        GUIUtil.renderBorder(matrices, getX() + 3, getY() + 3, width - 4 * 2 + 1, height - 4 * 2 + 1, borderCol, 0);

        GUIUtil.drawCenteredText(matrices, textRenderer, this.getMessage(), getX() + this.width / 2f, getY() + (this.height - 8) / 2f, textCol, false);
    }

    int getYImage(boolean toggled)
    {
        return toggled ? 2 : 1;
    }

    public interface ToggleAction
    {
        void onToggle(TextToggleWidget button, boolean toggled);
    }
}
