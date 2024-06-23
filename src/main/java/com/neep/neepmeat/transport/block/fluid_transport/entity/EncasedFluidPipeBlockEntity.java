package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.transport.block.EncasedBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.EncasedFluidPipeBlock;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class EncasedFluidPipeBlockEntity<T extends PipeVertex & NbtSerialisable> extends FluidPipeBlockEntity<T> implements EncasedBlockEntity
{
    private BlockState camoState = Blocks.AIR.getDefaultState();
    @Nullable private VoxelShape cachedShape;

    public EncasedFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<T> constructor)
    {
        super(type, pos, state, constructor);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        var nbt = super.toInitialChunkDataNbt();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("camo_state", NbtHelper.fromBlockState(camoState));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.camoState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("camo_state"));
    }

    @Override
    public BlockState getCamoState()
    {
        return camoState;
    }

    @Override
    public void setCamoState(BlockState camoState)
    {
        this.camoState = camoState;
        this.cachedShape = null;
    }

    @Override
    public VoxelShape getCamoShape()
    {
        if (cachedShape == null)
        {
            cachedShape = VoxelShapes.fullCube();
            if (getCamoState().isOf(getCachedState().getBlock()) || getCamoState().isAir())
            {
                return cachedShape;
            }
            else
            {
                if (getCachedState().getBlock() instanceof EncasedFluidPipeBlock block)
                {
                    cachedShape = block.getPipeOutlineShape(getCachedState(), world, getPos());
                }

                VoxelShape camoShape = camoState.getOutlineShape(world, pos);
                cachedShape = VoxelShapes.union(camoShape, cachedShape);
            }
        }
        return cachedShape;
    }
}
