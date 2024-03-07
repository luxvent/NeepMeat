package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    public MeatgunComponentImpl(ItemStack stack)
    {
        super(stack);
    }

    private final BaseModule root = new BaseModule();

    @Override
    public MeatgunModule getRoot()
    {
        return root;
    }

    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.trigger(world, player, stack, id, pitch, yaw, handType);
    }
}
