package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.MWExtraModels;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class MeatgunRenderer extends BuiltinModelItemRenderer
{
    private ItemColors colors = new ItemColors();

    public MeatgunRenderer()
    {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.MEATGUN_BASE);
        BakedModel thing = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.MEATGUN_BOSHER);

        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, thing);
    }

    public void renderItem(
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BakedModel model
    )
    {
        if (!stack.isEmpty())
        {
            matrices.push();

//            matrices.translate(1F, 1F, 1F);
//            model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
//            matrices.translate(-0.5F, -0.5F, -0.5F);
//            matrices.translate(0.5F, 0.5F, 0.5F);
            boolean bl2 = false;

            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

            renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);

            matrices.pop();
        }
    }

    private void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices)
    {
        Random random = Random.create();
        long l = 42L;

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, random), stack, light, overlay);
        }

        random.setSeed(42L);
        this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay)
    {
        MatrixStack.Entry entry = matrices.peek();

        for (BakedQuad bakedQuad : quads)
        {
            int i = -1;
            if (bakedQuad.hasColor())
            {
                i = this.colors.getColor(stack, bakedQuad.getColorIndex());
            }

            float f = (float) (i >> 16 & 0xFF) / 255.0F;
            float g = (float) (i >> 8 & 0xFF) / 255.0F;
            float h = (float) (i & 0xFF) / 255.0F;
            vertices.quad(entry, bakedQuad, f, g, h, light, overlay);
        }
    }
}
