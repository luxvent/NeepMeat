package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface MeatgunComponent extends Component
{
    MeatgunModule getRoot();

    UUID getUuid();

    void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    RecoilManager getRecoil();

    void tick(PlayerEntity player);

    void markDirty();

    @Nullable
    MeatgunModule find(UUID uuid);

    Listener getListener();

    interface Listener
    {
        MeatgunComponent get();

        PacketByteBuf getBuf(MeatgunModule module);

        void send(PlayerEntity player, PacketByteBuf buf);

        void markDirty();
    }
}
