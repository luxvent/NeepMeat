package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.block.pipe.IItemPipe;
import com.neep.neepmeat.block.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.blockentity.machine.EjectorBlockEntity;
import com.neep.neepmeat.blockentity.machine.ItemPumpBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemInPipe;
import com.neep.neepmeat.util.MiscUitls;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EjectorBlock extends BaseFacingBlock implements BlockEntityProvider, IItemPipe
{
    public EjectorBlock(String registryName, int itemMaxStack, boolean hasLore, FabricBlockSettings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque().solidBlock(ContentDetectorBlock::never));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.EJECTOR.instantiate(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUitls.checkType(type, NMBlockEntities.EJECTOR, EjectorBlockEntity::serverTick, world);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be && !world.isClient)
        {
            be.markNeedsRefresh();
            be.updateRedstone(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    // TODO: make this do things
    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
        {
        }
        return 0;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return direction.equals(state.get(FACING));
    }

    @Override
    public List<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Direction facing = state.get(FACING);
        return List.of(facing, facing.getOpposite()).stream().filter(forbidden).collect(Collectors.toList());
    }
}
