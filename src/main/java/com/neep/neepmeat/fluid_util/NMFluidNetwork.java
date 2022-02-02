package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class NMFluidNetwork
{
    private World world;
    private BlockPos origin;
    private Direction originFace;
    public static int UPDATE_DISTANCE = 5;

    public HashSet<Supplier<FluidNode>> connectedNodes = new HashSet<>();

    private final Map<BlockPos, PipeSegment> networkPipes = new HashMap<>();
    private final List<BlockPos> pipeQueue = new ArrayList<>();

    // My pet memory leak.
    public static List<NMFluidNetwork> LOADED_NETWORKS = new ArrayList<>();

    static
    {
        ServerTickEvents.END_SERVER_TICK.register(NMFluidNetwork::tickNetwork);
    }

    private NMFluidNetwork(World world, BlockPos origin, Direction direction)
    {
        this.world = world;
        this.origin = origin;
        this.originFace = direction;
    }

    public static Optional<NMFluidNetwork> tryCreateNetwork(World world, BlockPos pos, Direction direction)
    {
        NMFluidNetwork network = new NMFluidNetwork(world, pos, direction);
        network.rebuild(pos, direction);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
        return Optional.empty();
    }

    private static void tickNetwork(MinecraftServer minecraftServer)
    {
        LOADED_NETWORKS.forEach(NMFluidNetwork::tick);
    }

    @Override
    public String toString()
    {
        return "\nFluidNetwork at " + (origin).toString();
    }

    public static void validateAll()
    {
        LOADED_NETWORKS.removeIf(current -> !current.isValid());
    }

    public boolean isValid()
    {
        if (connectedNodes.size() < 2)
            return false;

        int count = 0;
        for (Iterator<Supplier<FluidNode>> iterator = connectedNodes.iterator(); iterator.hasNext(); )
        {
            Supplier<FluidNode> supplier = iterator.next();
            if (supplier.get() == null)
            {
                iterator.remove();
                ++count;
            }
        }
//        System.out.println(count + " " + connectedNodes2.size());
        return connectedNodes.size() - count >= 2;
    }

    // Removes network and connected nodes if not valid.
    public boolean validate()
    {
        if (!isValid())
        {
            LOADED_NETWORKS.remove(this);
            connectedNodes.clear();
            return false;
        }
        return true;
    }

    public void rebuild(BlockPos startPos, Direction face)
    {
        if (!world.isClient)
        {
            discoverNodes(startPos, face);
            if (!validate())
                return;
            connectedNodes.forEach((node) -> node.get().setNetwork(this));
//            buildPressures();
//            tick();
        }
    }

    public void merge(NMFluidNetwork network)
    {

    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public void tick()
    {
        buildPressures();
//        rebuild(origin, originFace);
        for (Supplier<FluidNode> supplier : connectedNodes)
        {
            FluidNode node;
            if ((node = supplier.get()) == null)
            {
                continue;
            }
            for (Supplier<FluidNode> targetSupplier : connectedNodes)
            {
                FluidNode targetNode;
                if ((targetNode = targetSupplier.get()).equals(node) || targetSupplier.get() == null)
                {
                    continue;
                }
                node.transmitFluid(targetNode);
            }
        }
    }

    public void addNode(Supplier<FluidNode> node)
    {
        connectedNodes.add(node);
    }

    public void buildPressures()
    {
        try
        {
            // Set networks before updating distances.

            for (Supplier<FluidNode> supplier : connectedNodes)
            {
                FluidNode node = supplier.get();
                if (node == null)
                {
                    continue;
                }

                List<BlockPos> nextSet = new ArrayList<>();
                networkPipes.values().forEach((segment) -> segment.setVisited(false));

                pipeQueue.clear();
                pipeQueue.add(node.getPos());

                for (int i = 0; i < UPDATE_DISTANCE; ++i)
                {
//                for (ListIterator<PipeSegment> iterator = networkPipes.listIterator(); iterator.hasNext();)
                    for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext(); )
                    {
                        BlockPos current = iterator.next();
                        networkPipes.get(current).setDistance(i + 1);
                        networkPipes.get(current).setVisited(true);
                        for (Direction direction : networkPipes.get(current).connections)
                        {
                            if (networkPipes.containsKey(current.offset(direction)) && !networkPipes.get(current.offset(direction)).isVisited())
//                            if (networkPipes.containsKey(current.offset(direction)) && !visited.contains(current.offset(direction)))
                            {
                                nextSet.add(current.offset(direction));
                            }
                        }
                        iterator.remove();
                    }
                    pipeQueue.addAll(nextSet);
                    nextSet.clear();
                }

                // TODO: optimise further
                for (Supplier<FluidNode> supplier1 : connectedNodes)
                {
                    FluidNode node1 = supplier1.get();
                    if (node1 == null || node1.equals(node) || node1.mode == AcceptorModes.NONE)
                    {
                        continue;
                    }
                    int distanceToNode = networkPipes.get(node1.getPos()).getDistance();
                    node.distances.put(node1, distanceToNode);
//                    node.distances.put(node1, 1);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void discoverNodes(BlockPos startPos, Direction face)
    {
        networkPipes.clear();
        pipeQueue.clear();
        connectedNodes.clear();

        // List of pipes to be searched in next iteration
        List<BlockPos> nextSet = new ArrayList<>();

//        networkPipes.put(startPos.offset(face), new PipeSegment(startPos.offset(face), world.getBlockState(startPos.offset(face))));
//        pipeQueue.add(startPos.offset(face));
        pipeQueue.add(startPos);
        networkPipes.put(startPos, new PipeSegment(startPos));

        // Pipe search depth
        for (int i = 0; i < UPDATE_DISTANCE; ++i)
        {
            nextSet.clear();
            ListIterator<BlockPos> it = pipeQueue.listIterator();
            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            {
                BlockPos current = iterator.next();

                for (Direction direction : Direction.values())
                {
                    BlockPos next = current.offset(direction);
                    BlockState state1 = world.getBlockState(current);
                    BlockState state2 = world.getBlockState(next);

                    if (FluidAcceptor.isConnectedIn(state1, direction) && !networkPipes.containsValue(new PipeSegment(next)))
                    {
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof FluidAcceptor
                                && !(state2.getBlock() instanceof FluidNodeProvider))
                        {
                            // Next block is connected in opposite direction
                            if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                            {
                                nextSet.add(next);
                                networkPipes.put(next, new PipeSegment(next.toImmutable(), state2));
                            }
                        }
                        else if (state2.hasBlockEntity())
                        {
//                            BlockApiCache<Storage<FluidVariant>, Direction> cache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, next);
                            Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, next, direction.getOpposite());
                            if (storage != null)
                            {
                                Supplier<FluidNode> node = FluidNetwork.NETWORK.getNodeSupplier(new NodePos(current, direction));
                                if (node.get() != null)
                                {
                                    connectedNodes.add(node);
                                }
//                                FluidNetwork.NETWORK.
//                                    FluidNode node;
//                                    if (state2.getBlock() instanceof FluidNodeProvider provider)
//                                    {
//                                        node = provider.getNode(world, next, direction);
//                                    }
//                                    else
//                                    {
//                                        node = new FluidNode(next, direction.getOpposite(), storage, AcceptorModes.INSERT_EXTRACT, 0);
//                                    }
//                                    connectedNodes.add(node);
                            }
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }

        validate();
//        System.out.println("targets: " + connectedNodes);
    }

    public void removeNode(NodePos pos)
    {
        connectedNodes.remove(FluidNetwork.NETWORK.getNodeSupplier(pos));
    }
}