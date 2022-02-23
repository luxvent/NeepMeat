package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.ResourceSnapshotParticipant;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TrommelBlockEntity extends BlockEntity implements
        BlockEntityClientSerializable,
//        Storage<ItemVariant>,
        InventoryStorage,
        SingleSlotStorage<ItemVariant>
{
    private ItemVariant resource = ItemVariant.blank();
    private long amount;
    private final int capacity;
    private SnapshotParticipant<ResourceAmount<ItemVariant>> snapshot =
            new ResourceSnapshotParticipant<>(this::getResource, this::getAmount, this::setResource, this::setAmount);

    public TrommelBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.TROMMEL_BLOCK_ENTITY, pos, state);
        capacity = 64;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putLong("amount", getAmount());
        tag.put("resource", getResource().toNbt());
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        this.amount = tag.getLong("amount");
        this.resource = ItemVariant.fromNbt(tag.getCompound("resource"));
        super.readNbt(tag);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (getResource() == null || getResource().isBlank() || getAmount() <= 0)
        {
            this.amount = 0;
            this.resource = resource;
        }

        long inserted = Math.min(maxAmount, getCapacity() - getAmount());

        if (getResource().equals(resource) && inserted > 0)
        {
            snapshot.updateSnapshots(transaction);
            amount += inserted;
            markDirty();
            sync();
            return inserted;
        }
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (getResource() == null || getResource().isBlank() || getAmount() <= 0)
        {
            amount = 0;
            return 0;
        }

        long extracted = Math.min(maxAmount, getAmount());

        if (extracted > 0 && resource.equals(getResource()))
        {
            snapshot.updateSnapshots(transaction);
            amount -= extracted;
            markDirty();

            if (getAmount() <= 0)
            {
                this.resource = ItemVariant.blank();
            }

            sync();

            return extracted;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank()
    {
        return resource.isBlank();
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        return SingleViewIterator.create(this, transaction);
    }

    public boolean extractFromItem(ItemEntity itemEntity)
    {
        boolean success = false;
        ItemStack itemStack = itemEntity.getStack();

        Transaction transaction = Transaction.openOuter();

        int transferred = (int) insert(ItemVariant.of(itemStack), itemStack.getCount(), transaction);
        itemStack.decrement(transferred);
        if (itemStack.getCount() <= 0)
        {
//            itemEntity.remove(Entity.RemovalReason.DISCARDED);
            itemEntity.discard();
        }

        transaction.commit();

        return success;
    }

    public ItemVariant getResource()
    {
        return resource;
    }

    public void setResource(ItemVariant resource)
    {
        this.resource = resource;
    }

    public long getAmount()
    {
        return amount;
    }

    public void setAmount(long amount)
    {
        this.amount = amount;
    }

    @Override
    public long getCapacity()
    {
        return capacity;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return writeNbt(tag);
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots()
    {
        return List.of(this);
    }
}