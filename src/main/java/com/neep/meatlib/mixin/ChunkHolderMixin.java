package com.neep.meatlib.mixin;

import com.neep.meatlib.blockentity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin
{
    @Shadow protected abstract void sendPacketToPlayers(List<ServerPlayerEntity> players, Packet<?> packet);

    @Inject(method = "sendBlockEntityUpdatePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toUpdatePacket()Lnet/minecraft/network/packet/Packet;"), cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void sendBECSUpdatePacket(List<ServerPlayerEntity> players, World world, BlockPos pos, CallbackInfo ci, BlockEntity blockEntity)
    {
        if (blockEntity instanceof BlockEntityClientSerializable becs)
        {
            Packet<?> packet = blockEntity.toUpdatePacket();
            if (packet != null)
            {
                sendPacketToPlayers(players, packet);
            }
            else
            {
                becs.sendUpdatePacket(players);
            }

            ci.cancel();
        }
    }
}
