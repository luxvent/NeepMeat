package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.recipe.ItemWorkpiece;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class RemoveInstruction implements Instruction
{
    private final Supplier<World> world;
    private final Argument from;

    public RemoveInstruction(Supplier<World> world, List<Argument> arguments)
    {
        this.world = world;
        this.from = arguments.get(0);
    }

    public RemoveInstruction(Supplier<World> world, NbtCompound compound)
    {
        this.world = world;
        this.from = Argument.fromNbt(compound.getCompound("target"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", from.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(GroupedRobotAction.of(
                new RobotMoveToAction(from.pos())
        ), this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    private void finish(PLC plc)
    {
        MutateInPlace<?> mip = MutateInPlace.ITEM.find(world.get(), from.pos(), null);
        if (mip == null)
        {
            mip = MutateInPlace.ENTITY.find(world.get(), from.pos(), null);
        }

        if (mip != null)
        {
            var object = mip.get();
            if (object instanceof ItemStack stack && !ItemWorkpiece.has(stack))
            {
                return;
            }

            Workpiece workpiece;
            if (object != null && (workpiece = NMComponents.WORKPIECE.getNullable(object)) != null)
            {
                if (workpiece.getSteps().size() > 0)
                    workpiece.removeStep(workpiece.getSteps().size() - 1);
            }

            ((MutateInPlace<Object>) mip).set(object);

        }

        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.REMOVE;
    }
}
