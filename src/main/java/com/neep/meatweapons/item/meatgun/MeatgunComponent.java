package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface MeatgunComponent extends Component
{
    MeatgunModule getRoot();

    void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    RecoilManager getRecoil();

    void tick();

    void markDirty();
}
