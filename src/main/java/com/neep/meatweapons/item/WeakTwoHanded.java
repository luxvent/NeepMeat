package com.neep.meatweapons.item;

import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface WeakTwoHanded
{
    default boolean displayArmFirstPerson(ItemStack stack, Hand hand)
    {
        return true;
    }

    default ArmPose getThirdPersonArmPose(AbstractClientPlayerEntity player, ItemStack stackInHand, Hand hand)
    {
        return ArmPose.BOW_AND_ARROW;
    }

    enum ArmPose
    {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true),
        SPYGLASS(false),
        TOOT_HORN(false),
        BRUSH(false);

        private final boolean twoHanded;

        ArmPose(boolean twoHanded)
        {
            this.twoHanded = twoHanded;
        }

        public boolean isTwoHanded()
        {
            return this.twoHanded;
        }
    }
}
