package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MotorBlockEntity extends BloodMachineBlockEntity
{
    public boolean running;
    public boolean starting;
    public int startTicks = 20;
    public int stopTicks = 40;
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 1 * FluidConstants.BUCKET, 1 * FluidConstants.BUCKET);
    }

    public MotorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MOTOR, pos, state);
    }

    @Override
    public long doWork(long amount, TransactionContext transaction)
    {
        long converted = super.doWork(amount, transaction);
        this.running = converted == amount;

        return converted;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
        sync();
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putBoolean("running", running);
        return nbt;
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.running = nbt.getBoolean("running");
    }

    public void update(World world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(BaseFacingBlock.FACING);
        if (!(world.getBlockEntity(pos.offset(facing)) instanceof IMotorisedBlock))
        {
            setRunning(false);
        }
    }
}
