package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.transport.item_network.RoutingNetworkCache;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class ItemPipeBlockEntity extends SyncableBlockEntity
{
    protected List<ItemInPipe> items = new ArrayList<>();
    protected RoutingNetworkCache cache = new RoutingNetworkCache();
    protected List<BlockApiCache<Storage<ItemVariant>, Direction>> storageCaches = Arrays.asList(new BlockApiCache[6]);
    public int lastOutput;

    public ItemPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.PNEUMATIC_PIPE, pos, state);
    }

    public ItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public List<ItemInPipe> getItems()
    {
        return items;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);

        NbtList itemList = new NbtList();
        for (ItemInPipe offset : items)
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1 = offset.toNbt(nbt1);
            itemList.add(nbt1);
        }

        tag.put("items", itemList);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);

        NbtList itemList = (NbtList) tag.get("items");
        int size = itemList != null ? itemList.size() : 0;
        items.clear();
        for (int i = 0; i < size; ++i)
        {
            items.add(ItemInPipe.fromNbt(itemList.getCompound(i)));
        }
    }
    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, ItemPipeBlockEntity be)
    {
        if (be.items.isEmpty())
            return;

        Iterator<ItemInPipe> it = be.items.listIterator();
        while (it.hasNext())
        {
            ItemInPipe item = it.next();
            item.tick();
            if (item.progress >= 1)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    long transferred = ItemPipeUtil.pipeToAny(item, blockPos, item.out, world, transaction, false);
                    if (transferred == item.amount() || item.getItemStack().isEmpty())
                    {
                        it.remove();
                    }
                    else
                    {
                        ItemPipeUtil.bounce(item, world, blockState);
                    }
                    transaction.commit();
                }
            }
        }
        be.sync();
    }

    public long insert(ItemInPipe item, World world, BlockState state, BlockPos pos, Direction in, TransactionContext transaction)
    {
        Direction out = ((ItemPipe) getCachedState().getBlock()).getOutputDirection(item, pos, state, world, in, this, transaction);
        transaction.addOuterCloseCallback(r ->
        {
            if (!r.wasCommitted())
                return;

//            Direction out = getOutputDirection(item, state, world, in);
            item.reset(in, out, world.getTime());
            this.items.add(item);
        });
        return item.getItemStack().getCount();
    }

//    private Direction getOutputDirection(ItemInPipe item, BlockState state, World world, Direction in)
//    {
//        Set<Direction> connections = ((ItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);
//
//        Direction out = item.getPreferredOutputDirection(state, in, (ItemPipe) getCachedState().getBlock());
//        if (out != null && connections.contains(out))
//            return out;
//
//        var rand = world.getRandom();
//        if (!connections.isEmpty())
//        {
//            out = Iterables.get(connections, rand.nextInt(connections.size()));
////            out = connections.get(rand.nextInt(connections.size()));
//        }
//        else
//        {
//            out = in;
//        }
//        return out;
//    }

    public void dropItems()
    {
        for (ItemInPipe item : items)
        {
            Entity itemEntity = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, item.getItemStack());
            getWorld().spawnEntity(itemEntity);
        }
        items.clear();
    }

    public RoutingNetworkCache getCache()
    {
        return cache;
    }

    @Nullable
    public Storage<ItemVariant> getStorage(Direction localDirection)
    {
        int id = localDirection.ordinal();
        if (storageCaches.get(id) == null)
        {
            storageCaches.set(id, BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, pos.offset(localDirection)));
        }
        return storageCaches.get(id).find(localDirection.getOpposite());
    }

    public int getCurrentOutput(Set<Direction> connections)
    {
        if (!connections.contains(Direction.values()[lastOutput]))
            return nextOutput(connections);

        return lastOutput;
    }

    public int nextOutput(Set<Direction> connections)
    {
        lastOutput = (lastOutput + 1) % 6;
        while (!connections.contains(Direction.values()[lastOutput]))
            lastOutput = (lastOutput + 1) % 6;

        return lastOutput;
    }
}