package com.neep.neepmeat.client.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.object.Color;

public interface GUIUtil
{
    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height)
    {
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height, int col)
    {
        Color color = new Color(col);
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int u, int v, int width, int height, float r, float g, float b, float a)
    {
        drawTexture(texture, context, x, y, 0, (float) u, (float) v, width, height, 256, 256, r, g, b, a);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)
    {
        drawTexture(texture, context, x, x + width, y, y + height, 0, width, height, u, v, textureWidth, textureHeight, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight, float r, float g, float b, float a)
    {
        drawTexture(texture, context, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight, r, g, b, a);
    }

    static void drawTextureStretch(Identifier texture, DrawContext context, int x1, int y1, int w, int h, float u, float v, int du, int dv, int textureWidth, int textureHeight)
    {
        drawTexturedQuad(texture, context, x1, x1 + w, y1, y1 + h, 0, u / textureWidth, (u + du) / textureWidth, v / textureHeight, (v + dv) / textureHeight, 1, 1, 1, 1);
    }

    static void drawTexture(Identifier texture, DrawContext context, int x1, int x2, int y1, int y2, int z, int du, int dv, float u, float v, int textureWidth, int textureHeight, float r, float g, float b, float a)
    {
        drawTexturedQuad(texture, context, x1, x2, y1, y2, z, u / textureWidth, (u + du) / textureWidth, v / textureHeight, (v + dv) / textureHeight, r, g, b, a);
    }

    private static void drawTexturedQuad(Identifier texture, DrawContext context, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float r, float g, float b, float a)
    {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(r, g, b, a).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).color(r, g, b, a).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).color(r, g, b, a).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).color(r, g, b, a).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    static void renderBorder(DrawContext context, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);
        drawVerticalLine1(context, x + dx + offset, y - offset, y + dy + offset, col);
    }

    static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, float centerX, float y, int color, boolean shadow)
    {
//        OrderedText orderedText = text.asOrderedText();
        drawText(context, textRenderer, text, centerX - textRenderer.getWidth(text) / 2f, y, color, shadow);
    }


    static int drawText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, OrderedText text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        return i;
    }

    static void drawHorizontalLine1(DrawContext context, int x1, int x2, int y, int color)
    {
        if (x2 < x1)
        {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        context.fill(x1, y, x2 + 1, y + 1, color);
    }

    static void drawVerticalLine1(DrawContext context, int x, int y1, int y2, int color)
    {
        if (y2 < y1)
        {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        context.fill(x, y1 + 1, x + 1, y2, color);
    }

}
