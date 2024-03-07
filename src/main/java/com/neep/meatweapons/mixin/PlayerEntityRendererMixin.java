package com.neep.meatweapons.mixin;

import com.neep.meatweapons.client.renderer.ArmPoseHelper;
import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.WeakTwoHanded;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin
{
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir)
    {
        Item item = player.getStackInHand(hand).getItem();
        if (item instanceof WeakTwoHanded weakTwoHanded)
        {
            cir.setReturnValue(ArmPoseHelper.getPose(weakTwoHanded.getThirdPersonArmPose(player, player.getStackInHand(hand), hand)));
        }
        else if (item instanceof BaseGunItem || item instanceof AssaultDrillItem)
        {
            cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
