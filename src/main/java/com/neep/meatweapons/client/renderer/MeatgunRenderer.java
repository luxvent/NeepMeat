package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderer;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunRenderers;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class MeatgunRenderer extends BuiltinModelItemRenderer
{
    public MeatgunRenderer()
    {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay)
    {
//        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
//        BakedModel thing = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.MEATGUN_CHUGGER);

        MeatgunComponent component = MWComponents.MEATGUN.get(stack);

        var root = component.getRoot();
        MeatgunModuleRenderer renderer = MeatgunRenderers.get(root);
        renderer.render(stack, component, root, mode, matrices, vcp, light, overlay);

//        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, thing);
    }

    private void renderRoot(MatrixStack matrices, ModelTransformationMode mode, MeatgunModule module)
    {
        if (module == MeatgunModule.DEFAULT)
            return;
    }
}
