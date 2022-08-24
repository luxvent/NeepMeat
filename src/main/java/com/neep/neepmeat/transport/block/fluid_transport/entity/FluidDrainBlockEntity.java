package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.neepmeat.fluid.RealisticFluid;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("UnstableApiUsage")
public class FluidDrainBlockEntity extends TankBlockEntity
{
    private int transferCooldown = 0;

    public FluidDrainBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.FLUID_DRAIN, pos, state);
    }

    public boolean needsCooldown()
    {
        return this.transferCooldown > 0;
    }

    private void setCooldown(int cooldown)
    {
        this.transferCooldown = cooldown;
    }

    public boolean grabFluid(World world, BlockPos pos, BlockState state)
    {
        BlockPos upPos = pos.up();
        BlockPos sourcePos = findFluid(world, upPos, state);
        if (sourcePos != null)
        {
            FluidState fluidState = world.getFluidState(sourcePos);
            Transaction transaction = Transaction.openOuter();
            BlockState fluidBlockState = world.getBlockState(sourcePos);
            long targetAmount = world.getFluidState(sourcePos).getLevel() * FluidConstants.BUCKET / 8;
            long transferred = this.buffer.insert(FluidVariant.of(((FlowableFluid) fluidState.getFluid()).getStill()), targetAmount, transaction);
            if (transferred >= targetAmount)
            {

                if (world.getFluidState(sourcePos).getFluid() instanceof RealisticFluid realisticFluid)
                {
                    world.setBlockState(sourcePos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    transaction.commit();
                }
                else if (world.getBlockState(sourcePos).getBlock() instanceof FluidDrainable drainable)
                {
                    drainable.tryDrainFluid(world, sourcePos, fluidBlockState);
                    transaction.commit();
                }
            }
            else
            {
                transaction.abort();
            }
       }
        return true;
    }

    protected BlockPos findFluid(World world, BlockPos origin, BlockState state)
    {
        if (world.getFluidState(origin).isEmpty())
        {
            return null;
        }
        else if (world.getFluidState(origin).isStill() || world.getFluidState(origin).getFluid() instanceof RealisticFluid)
        {
            return origin;
        }

        List<BlockPos> visited = new ArrayList<>();
        List<BlockPos> next = new ArrayList<>();
        next.add(origin);

        for (int i = 0; i < 8; ++i)
        {
            for (ListIterator<BlockPos> it = next.listIterator(); it.hasNext(); )
            {
                BlockPos current = it.next();
                visited.add(current);
                it.remove();
                for (Direction direction : Direction.values())
                {
                    BlockPos newPos = current.offset(direction);
                    FluidState fluidState = world.getFluidState(newPos);

                    if (visited.contains(newPos) || fluidState.isEmpty())
                        continue;

                    if (fluidState.isStill() || fluidState.getFluid() instanceof RealisticFluid)
                    {
                        return newPos;
                    }
                    else
                    {
                        it.add(newPos);
                    }
                }
            }
        }
        return null;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FluidDrainBlockEntity blockEntity)
    {
        --blockEntity.transferCooldown;
        if (!blockEntity.needsCooldown())
        {
            blockEntity.setCooldown(16);
            blockEntity.grabFluid(world, pos, state);
        }
    }
}