package com.neep.neepmeat.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.blockentity.pipe.RouterBlockEntity;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RouterBlock extends BaseBlock implements BlockEntityProvider, IItemPipe
{
    public RouterBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item)
    {
        return 1;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new RouterBlockEntity(pos, state);
    }
}
