package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.api.plc.robot.SoundAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.item.ItemImplantItem;
import com.neep.neepmeat.network.ParticleSpawnS2C;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import com.neep.neepmeat.plc.recipe.PLCRecipes;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class CombineInstruction implements Instruction
{
    private final Supplier<World> worldSupplier;
    protected Argument from;
    protected Argument to;
    private final GroupedRobotAction group;

    public CombineInstruction(Supplier<World> world, List<Argument> arguments)
    {
        this.worldSupplier = world;
        this.from = arguments.get(0);
        this.to = arguments.get(1);

        group = GroupedRobotAction.of(
                new RobotMoveToAction(from.pos()),
                new SoundAction(world, SoundEvents.BLOCK_BEEHIVE_EXIT),
                AtomicAction.of(this::takeFirst),
                new RobotMoveToAction(to.pos()),
                AtomicAction.of(this::complete)
        );
    }

    public CombineInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this(world, List.of(
                Argument.fromNbt(nbt.getCompound("from")),
                Argument.fromNbt(nbt.getCompound("to"))
        ));
        group.readNbt(nbt.getCompound("action"));
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(group, this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {
        plc.getActuator().dumpStored(plc);
        group.end(plc);
    }

    private void takeFirst(PLC plc)
    {
        var stored = takeItem(LazyBlockApiCache.itemSided(from, worldSupplier));
        if (stored == null)
        {
            plc.raiseError(new PLC.Error(Text.of("Oh noes!")));
        }
        else
        {
            plc.getActuator().setStored(plc, stored);
            if (worldSupplier.get() instanceof ServerWorld serverWorld)
            {
                ParticleSpawnS2C.sendNearby(serverWorld, from.pos(), new ItemStackParticleEffect(ParticleTypes.ITEM, stored.resource().toStack()),
                        Vec3d.ofCenter(from.pos()), new Vec3d(0, 0.3, 0), new Vec3d(0.1, 0.1, 0.1), 4);
            }
        }

    }

    private void complete(PLC plc)
    {
        final var stored = plc.getActuator().getStored(plc);
        var mip = MutateInPlace.ITEM.find(worldSupplier.get(), to.pos(), null);
        if (stored != null && mip != null && mip.get() != null)
        {
            ItemStack stack = mip.get();

            var step = CombineStep.get(stored.resource().toStack((int) stored.amount()));

            // TODO: This is a teMpORaRy measure because I can't be bothered to add ANOTHER recipe type
            if (stored.resource().getObject() instanceof ItemImplantItem item)
            {
                item.install(stack);
                plc.getActuator().setStored(plc, null);
                mip.set(stack);
            }

            var workpiece = NMComponents.WORKPIECE.maybeGet(stack).orElse(null);

            if (workpiece != null && PLCRecipes.isValidStep(PLCRecipes.MANUFACTURE, workpiece, step, stack.getItem()))
            {
                workpiece.addStep(step);

                mip.set(stack);

                ItemManufactureRecipe recipe = MeatlibRecipes.getInstance().getFirstMatch(PLCRecipes.MANUFACTURE, mip).orElse(null);
                if (recipe != null)
                {
                    recipe.ejectOutputs(mip, null);
                    workpiece.clearSteps();
                }

                if (worldSupplier.get() instanceof ServerWorld serverWorld)
                {
                    var robot = plc.getActuator();
//                    ParticleSpawnS2C.sendNearby(serverWorld, plc.getRobot().getBlockPos(), new ItemStackParticleEffect(ParticleTypes.ITEM, stored.resource().toStack()),
//                            plc.getRobot().getPos(), new Vec3d(0, -0.4, 0), new Vec3d(0.1, 0.1, 0.1), 6);

                    serverWorld.playSound(null, robot.getX(), robot.getY(), robot.getZ(), NMSounds.COMBINE_INSTRUCTION_APPLY, SoundCategory.NEUTRAL, 1, 1, 1);
                }

                plc.getActuator().setStored(plc, null);

                return;
            }
        }

        plc.getActuator().dumpStored(plc);
    }

    private ResourceAmount<ItemVariant> takeItem(LazyBlockApiCache<Storage<ItemVariant>, Direction> target)
    {
        var storage = target.find();
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), found.amount(), transaction);
                    if (extracted > 0)
                    {
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }

                    transaction.abort();
                }
            }
        }

        return null;
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.COMBINE;
    }

    void finish(PLC plc)
    {
        plc.advanceCounter();
//        System.out.println("Finish");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("from", from.toNbt());
        nbt.put("to", to.toNbt());
        nbt.put("action", group.writeNbt(new NbtCompound()));
        return nbt;
    }

    // Here's the question: createFromNbt or readNbt
    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}