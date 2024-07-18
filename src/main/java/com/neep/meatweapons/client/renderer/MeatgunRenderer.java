package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderer;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderers;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunParticleManager;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunModule;
import com.neep.meatweapons.mixin.HeldItemRendererAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MeatgunRenderer extends BuiltinModelItemRenderer
{
    public MeatgunRenderer()
    {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    public static void transformRecoil(MatrixStack matrices, RecoilManager recoil)
    {
        matrices.translate(0, 0, recoil.horAmount);
        matrices.translate(0, 0, 1.4);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(recoil.amount));
        matrices.translate(0, 0, -1.4);
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay)
    {
        // Remove the transformations applied by ItemRenderer
        matrices.pop();
        matrices.push();

        MinecraftClient client = MinecraftClient.getInstance();
        AbstractClientPlayerEntity player = client.player;
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);

        boolean mainHand = ((HeldItemRendererAccessor) client.gameRenderer.firstPersonRenderer).getMainHand().equals(stack);
        boolean leftHanded = mode.isFirstPerson()
                && (player.getMainArm() == Arm.LEFT && mainHand || player.getMainArm() == Arm.RIGHT && !mainHand);

        // Apply the display transformations
        Transformation transformation = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack).getTransformation().getTransformation(mode);
        transformation.apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);

        // Step recoil
        MeatgunComponent component = MWComponents.MEATGUN.get(stack);
        RecoilManager recoil = component.getRecoil();
        if (mode.isFirstPerson())
        {
            float lastFrame = !client.isPaused() ? client.getLastFrameDuration() : 0;
            recoil.horAmount = Math.max(0, recoil.horAmount - recoil.horReturnSpeed * lastFrame);
            recoil.amount = Math.max(0, recoil.amount - recoil.returnSpeed * lastFrame);

            transformRecoil(matrices, recoil);
        }

        // Recursive module rendering
        matrices.push();
        var root = component.getRoot();
        renderRecursive(matrices, root, stack, component, mode, vcp,
                MinecraftClient.getInstance().world.getTime(),
                MinecraftClient.getInstance().getTickDelta(), light, overlay);
        matrices.pop();

        // Particles
        if (mode.isFirstPerson())
        {
            matrices.push();
            Camera camera = client.gameRenderer.getCamera();
            matrices.translate(0.5, 0, 1); // No idea why this transform is necessary, but this puts (0,0,0) at (8,0,16) in model coords.
//            transformRecoil(matrices, recoil);
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getEntityTranslucent(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE));
            for (var particle : MeatgunParticleManager.getParticles())
            {
                particle.render(matrices, camera, consumer, overlay, client.getTickDelta());
            }
            matrices.pop();
        }

        matrices.pop();
        matrices.push();

        Hand hand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
        Hand otherHand = mainHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (mode.isFirstPerson()
                && player.getStackInHand(otherHand).isEmpty()
                && hand == Hand.MAIN_HAND) // Prevent three arms when the main hand is empty
        {
            transformRecoil(matrices, recoil);
            if (leftHanded)
            {
                matrices.translate(0.6, -0.4, 0.2);
                renderArm(playerEntityRenderer.getModel().rightArm, true, matrices, player, vcp, light);
                renderArm(playerEntityRenderer.getModel().rightSleeve, true, matrices, player, vcp, light);
            }
            else
            {
                matrices.translate(-0.6, -0.4, 0.2);
                renderArm(playerEntityRenderer.getModel().leftArm, false, matrices, player, vcp, light);
                renderArm(playerEntityRenderer.getModel().leftSleeve, false, matrices, player, vcp, light);
            }
        }
    }

    private void renderArm(ModelPart armPart, boolean leftHanded, MatrixStack matrices, AbstractClientPlayerEntity player, VertexConsumerProvider vcp, int light)
    {
        armPart.pitch = (float) Math.toRadians(-120);
        armPart.yaw = (leftHanded ? -1 : 1) * MathHelper.PI / 6;
        armPart.roll = (float) Math.PI;
        armPart.render(matrices, vcp.getBuffer(RenderLayer.getEntityCutout(player.getSkinTexture())),
                light, OverlayTexture.DEFAULT_UV);
    }

    private <T extends MeatgunModule> void renderRecursive(MatrixStack matrices, T module, ItemStack stack, MeatgunComponent component,
                                                           ModelTransformationMode mode, VertexConsumerProvider vcp, long time, float tickDelta, int light, int overlay)
    {
        if (module == MeatgunModule.DEFAULT)
            return;

        MeatgunModuleRenderer<T> renderer = MeatgunModuleRenderers.get(module);
        renderer.render(stack, component, module, mode, matrices, vcp, time, tickDelta, light, overlay);

        for (var child : module.getChildren())
        {
            matrices.push();
            matrices.translate(0.5, 0, 1);
            Matrix4f matrix4f = child.transform(tickDelta);
            matrices.multiplyPositionMatrix(matrix4f);
            matrices.translate(-0.5, 0, -1);
            renderRecursive(matrices, child.get(), stack, component, mode, vcp, time, tickDelta, light, overlay);
            matrices.pop();
        }
    }

}
