package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.item.filter.TagFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import com.neep.neepmeat.util.TagSuggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;

public class TagFilterWidget extends FilterEntryWidget<TagFilter>
{
    public TagFilterWidget(int w, int index, TagFilter filter, FilterScreenHandler handler)
    {
        super(w, 41, index, filter, handler);
    }

    @Override
    public void init()
    {
        super.init();

        addDrawableChild(new TagTextField(MinecraftClient.getInstance().textRenderer, x() + 2, y() + 2, w - 4, 16, Text.empty()));
    }

    private static class TagTextField extends NMTextField
    {
        private List<Identifier> suggestions = List.of();

        public TagTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
        {
            super(textRenderer, x, y, width, height, text);
            setChangedListener(this::suggestTag);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (keyCode == GLFW.GLFW_KEY_TAB)
            {
                cycleSuggestion();
            }
            else if (keyCode == GLFW.GLFW_KEY_ENTER)
            {
                confirmSuggestion();
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
        {
            int borderCol = isSelected() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
            GUIUtil.renderBorderInner(context, x(), y(), w(), h(), borderCol, 0);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            super.renderButton(context, mouseX, mouseY, delta);

            int maxSuggestions = 5;
            if (!suggestions.isEmpty() && isActive())
            {
                int suggestionY = y2();

                int end = Math.min(suggestions.size(), maxSuggestions);
                int stride = textRenderer.fontHeight + 2;
                int yOffsetEnd = end * stride;
                context.fill(x(), y2(), x() + w(), y2() + yOffsetEnd, 0xBB331111);

                for (int i = 0; i < end; i++)
                {
                    Identifier suggestion = suggestions.get(i);
                    int yOffset = suggestionY + stride * i;
                    GUIUtil.drawText(context, textRenderer, suggestion.toString(), x() + textRenderer.getWidth("#") + 4, yOffset, PLCCols.BORDER.col, false);
                }

                GUIUtil.renderBorderInner(context, x(), y2(), w(), yOffsetEnd,  PLCCols.BORDER.col, 0);
            }
        }

        @Override
        protected int renderUnselectedText(DrawContext context, String string, boolean selectionWithin, int textStart, int m, int col, int j)
        {
            if (!suggestions.isEmpty())
            {
                GUIUtil.drawText(context, this.textRenderer, suggestions.get(0).toString(), textStart, m, PLCCols.INVALID.col, true);
            }

            return super.renderUnselectedText(context, string, selectionWithin, textStart, m, col, j);
        }

        @Override
        public String getPrefix()
        {
            return "#";
        }

        private void suggestTag(String current)
        {
            suggestions = TagSuggestions.INSTANCE.get(current);
        }

        private void cycleSuggestion()
        {
            Collections.rotate(suggestions,  1);
        }

        private void confirmSuggestion()
        {
            if (!suggestions.isEmpty())
                setText(suggestions.get(0).toString());
        }

        @Override
        protected int prefixCol()
        {
            return PLCCols.LINE_NUMBER.col;
        }
    }
}
