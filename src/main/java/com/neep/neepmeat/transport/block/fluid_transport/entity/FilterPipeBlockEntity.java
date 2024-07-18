package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.meatlib.blockentity.BlockEntityClientSerializable;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.fluid_transport.FilterPipeBlock;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class FilterPipeBlockEntity extends FluidPipeBlockEntity<FilterPipeBlock.FilterPipeVertex> implements BlockEntityClientSerializable
{
    protected FluidVariant filterVariant = FluidVariant.blank();

    public FilterPipeBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FILTER_PIPE, pos, state, FilterPipeBlock.FilterPipeVertex::new);
    }

    public FilterPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<FilterPipeBlock.FilterPipeVertex> constructor)
    {
        super(type, pos, state, constructor);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.filterVariant = FluidVariant.fromNbt(nbt.getCompound("filterVariant"));
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("filterVariant", filterVariant.toNbt());
    }

    public FluidVariant getFilterVariant()
    {
        return filterVariant;
    }

    public void setFilterFluid(FluidVariant variant)
    {
        filterVariant = variant;
        sync();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return createNbt();
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        readNbt(nbt);
    }

    @Override
    public void toClientTag(NbtCompound nbt)
    {
        writeNbt(nbt);
    }

    @Override
    public void onReceiveNbt(NbtCompound nbt)
    {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
    }
}
