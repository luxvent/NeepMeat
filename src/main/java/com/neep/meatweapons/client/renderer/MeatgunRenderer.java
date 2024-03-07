package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderer;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderers;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
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
        renderRecursive(matrices, root, stack, component, mode, vcp, light, overlay);
    }

    private <T extends MeatgunModule> void renderRecursive(MatrixStack matrices, T module, ItemStack stack, MeatgunComponent component,
                                 ModelTransformationMode mode, VertexConsumerProvider vcp, int light, int overlay)
    {
        if (module == MeatgunModule.DEFAULT)
            return;

        MeatgunModuleRenderer<T> renderer = MeatgunModuleRenderers.get(module);
        renderer.render(stack, component, module, mode, matrices, vcp, light, overlay);

        for (var child : module.getChildren())
        {
            renderRecursive(matrices, child, stack, component, mode, vcp, light, overlay);
        }
    }
}
