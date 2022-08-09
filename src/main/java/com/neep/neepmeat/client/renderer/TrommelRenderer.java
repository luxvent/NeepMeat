package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.trommel.TrommelBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class TrommelRenderer<T extends TrommelBlockEntity> implements BlockEntityRenderer<T>
{
    Model model;

    public TrommelRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseHorFacingBlock.FACING);

        matrices.translate(0.5, 0.5f, 0.5);
        matrices.scale(0.9f, 0.9f, 0.85f);
        Vec3f axis = facing.rotateYClockwise().getUnitVector();
//        matrices.multiply(axis.getDegreesQuaternion(10));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.rotateFacing(facing, matrices);
        BERenderUtils.renderModel(NMExtraModels.TROMMEL_MESH, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }
}