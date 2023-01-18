package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class BaseHorFacingBlock extends HorizontalFacingBlock implements IMeatBlock
{
    BlockItem blockItem;
    private final String registryName;

    public BaseHorFacingBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, itemName, itemSettings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
        this.registryName = itemName;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
//        return context.getSide().getAxis().isVertical() ? getDefaultState() :
//                this.getDefaultState().with(FACING, context.getSide().getOpposite());
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
//        Direction direction = state.get(FACING);
//        BlockPos blockPos = pos.offset(direction.getOpposite());
//        BlockState blockState = world.getBlockState(blockPos);
//        return blockState.isSideSolidFullSquare(world, blockPos, direction);
        return true;
//        return state.hasBlockEntity();
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
