package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlassTankBlockEntity extends BlockEntity implements FluidBufferProvider, BlockEntityClientSerializable
{
    private final FluidBuffer buffer;

    public GlassTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.GLASS_TANK_BLOCK_ENTITY, pos, state);
        this.buffer = new FluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNBT(tag);
    }


    @Override
    @Nullable
    public FluidBuffer getBuffer(Direction direction)
    {
//        return sideModes.get(direction) != FluidAcceptor.AcceptorModes NONE
//                || direction == null ? buffer : null;
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {

    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        buffer.readNBT(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return buffer.writeNBT(tag);
    }

    @Override
    public void sync()
    {
        World world = this.getWorld();
        if (world != null && !world.isClient)
        {
            BlockEntityClientSerializable.super.sync();
        }
    }
}