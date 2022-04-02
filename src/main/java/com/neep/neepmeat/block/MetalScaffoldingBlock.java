package com.neep.neepmeat.block;

import com.neep.neepmeat.api.block.BaseBlock;
import com.neep.neepmeat.api.block.BaseSlabBlock;
import com.neep.neepmeat.api.block.BaseStairsBlock;
import com.neep.neepmeat.api.block.NMBlock;
import com.neep.neepmeat.init.BlockInitialiser;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MetalScaffoldingBlock extends BaseBlock implements NMBlock, Waterloggable
{
    private final String registryName;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = Properties.BOTTOM;
    public final NMBlock stairs;
    public final NMBlock slab;

    public MetalScaffoldingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());

        stairs = new BaseStairsBlock(this.getDefaultState(),registryName + "_stairs", itemMaxStack, settings);
        BlockInitialiser.queueBlock(stairs);

        slab = new BaseSlabBlock(this.getDefaultState(),registryName + "_slab", itemMaxStack, settings);
        BlockInitialiser.queueBlock(slab);

        this.registryName = registryName;
        this.setDefaultState((this.stateManager.getDefaultState()).with(WATERLOGGED, false).with(BOTTOM, false));
        BlockInitialiser.queueBlock(this);
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        return (this.getDefaultState()
                .with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER))
                .with(BOTTOM, this.shouldBeBottom(world, blockPos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        state = state.with(BOTTOM, world.getBlockState(pos.up()).getBlock() instanceof MetalScaffoldingBlock);

        if (state.get(WATERLOGGED))
        {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isClient())
        {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, BOTTOM);
    }

    private boolean shouldBeBottom(BlockView world, BlockPos pos)
    {
        return world.getBlockState(pos.down()).isOf(this);
    }
}
