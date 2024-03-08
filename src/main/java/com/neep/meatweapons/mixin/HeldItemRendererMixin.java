package com.neep.meatweapons.mixin;

import com.neep.meatweapons.client.renderer.ArmRenderer;
import com.neep.meatweapons.item.WeakTwoHanded;
import myron.shaded.de.javagl.obj.Obj;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
    @Inject(method = "updateHeldItems", at = @At(value = "TAIL"))
    public void render(CallbackInfo ci)
    {
        // Using this.client.player returns null in unpredictable circumstances.
//        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
//        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
//        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
//
//        if (itemStack.getItem() instanceof BaseGunItem && ItemStack.areItemsEqualIgnoreDamage(mainHand, itemStack))
//        {
//            this.equipProgressMainHand = 1;
//            this.mainHand = itemStack;
//        }
//        if (itemStack2.getItem() instanceof BaseGunItem && ItemStack.areItemsEqualIgnoreDamage(offHand, itemStack2))
//        {
//            this.equipProgressOffHand = 1;
//            this.offHand = itemStack2;
//        }
    }

    // Causes offhand arm to render for certain weapons.
    @Inject(method = "renderFirstPersonItem", at = @At(value = "TAIL"))
    public void renderItemHead(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        ArmRenderer.onRenderArm(matrices, player, item, hand, ((HeldItemRenderer) (Object) this), equipProgress, swingProgress, vertexConsumers, light);
    }
}
