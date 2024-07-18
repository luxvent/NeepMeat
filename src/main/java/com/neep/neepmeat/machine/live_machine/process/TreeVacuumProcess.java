package com.neep.neepmeat.machine.live_machine.process;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.TreeVacuumBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.TreeVacuumBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;

public class TreeVacuumProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.TREE_VACUUM,
            LivingMachineComponents.ITEM_OUTPUT,
            LivingMachineComponents.MOTOR_PORT
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.TREE_VACUUM, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var vacuums = result.t1();
            var outputs = result.t2();
            var motors = result.t3();

            TreeVacuumBlockEntity vacuum = vacuums.iterator().next();
            World world = be.getWorld();

            vacuum.progress = Math.min(vacuum.progress + be.getProgressIncrement(), vacuum.maxProgress);

            if (vacuum.progress >= vacuum.maxProgress)
            {
                Direction facing = vacuum.getCachedState().get(TreeVacuumBlock.FACING);
                BlockPos trunkPos = vacuum.getPos().offset(facing, 2);

                if (MeatLib.vsUtil != null && MeatLib.vsUtil.hasShipAtPosition(be.getPos(), world))
                {
                    Vector3d temp = new Vector3d(0,0,0);
                    MeatLib.vsUtil.getShipToWorldMatrix(trunkPos, world).transformPosition(trunkPos.getX() + 0.5, trunkPos.getY() + 0.5, trunkPos.getZ() + 0.5, temp);
                    trunkPos = new BlockPos(MathHelper.floor(temp.x), MathHelper.floor(temp.y), MathHelper.floor(temp.z));

//                    if (world instanceof ServerWorld serverWorld)
//                    {
////                        serverWorld.spawnParticles(ParticleTypes.COMPOSTER, temp.x, temp.y, temp.z, 10, 0.2, 0.2, 0.2, 0.1);
//                        serverWorld.spawnParticles(ParticleTypes.COMPOSTER, trunkPos.getX() + 0.5, trunkPos.getY() + 0.5, trunkPos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.1);
//                    }
                }

                try (Transaction transaction = Transaction.openOuter())
                {
                    vacuum.progress = 0;
                    boolean broken = traverseTree(world, trunkPos, 300, 7, be.getCombinedItemOutput(), transaction);
                    vacuum.syncAnimation(broken);
                    transaction.commit();
                }
            }
        });
    }

    private boolean traverseTree(World world, BlockPos origin, int maxVisit, int maxBreak, Storage<ItemVariant> output, TransactionContext transaction)
    {
        Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN};

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        if (!isTree(world.getBlockState(origin)))
            return false;

        visited.add(origin);
        queue.add(origin);

        int treeBlocksVisited = 0;
        int treeBlocksBroken = 0;
        while (!queue.isEmpty()
                && treeBlocksVisited < maxVisit
                && treeBlocksBroken < maxBreak
        )
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            boolean foundOther = false;
            for (Direction direction : directions)
            {
                mutable.set(current, direction);
                if (!visited.contains(mutable))
                {
                    visited.add(mutable.toImmutable());

                    BlockState offsetState = world.getBlockState(mutable);
                    if (isTree(offsetState))
                    {
                        foundOther = true;
                        treeBlocksVisited++;
                        queue.add(mutable.toImmutable());
                    }
                }
            }

            if (!foundOther)
            {
                List<ItemStack> dropped = Block.getDroppedStacks(world.getBlockState(current), (ServerWorld) world, current, world.getBlockEntity(current));
                for (var stack : dropped)
                {
                    output.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                }

                world.breakBlock(current, false);
                treeBlocksBroken++;
            }
        }
        return treeBlocksBroken > 0;
    }

    private static boolean isTree(BlockState state)
    {
        Block block = state.getBlock();
        return
                !(block instanceof TreeVacuumBlock)
                && !(block instanceof TreeVacuumBlock.Structure)
                && (block instanceof LeavesBlock
                    || state.isIn(BlockTags.LOGS)
                    || state.isIn(BlockTags.LEAVES));
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return REQUIRED;
    }

    @Override
    public Text getName()
    {
        return Text.of("Tree Vacuum");
    }
}
