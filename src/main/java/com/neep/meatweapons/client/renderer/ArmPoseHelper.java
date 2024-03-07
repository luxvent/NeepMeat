package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.item.WeakTwoHanded;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;

@Environment(EnvType.CLIENT)
public class ArmPoseHelper
{
    public static BipedEntityModel.ArmPose getPose(WeakTwoHanded.ArmPose pose)
    {
        return BipedEntityModel.ArmPose.values()[pose.ordinal()];
    }
}
