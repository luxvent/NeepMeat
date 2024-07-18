package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlock;
import com.neep.neepmeat.util.ItemUtil;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrinderBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public GrinderBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque().solidBlock(InventoryDetectorBlock::never));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be)
        {
            if (player.isSneaking() && player.getMainHandStack().isEmpty())
            {
                if (world.isClient()) return ActionResult.SUCCESS;

                be.ejectXP();
                return ActionResult.SUCCESS;
            }
            return ActionResult.success(ItemUtil.singleVariantInteract(player, hand, be.getStorage().getInputStorage()));
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new GrinderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.CRUSHER, null, (world1, pos, state1, blockEntity) -> blockEntity.clientTick(), world);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()) && !world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be)
            {
                be.storage.dropItems(world, pos);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }



    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        super.onEntityCollision(state, world, pos, entity);
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be && !world.isClient() && entity instanceof ItemEntity item &&  entity.isOnGround())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ItemVariant variant = ItemVariant.of(item.getStack());
                long inserted = be.getStorage().getInputStorage().insert(variant, item.getStack().getCount(), transaction);
                item.getStack().decrement((int) inserted);
                transaction.commit();
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof GrinderBlockEntity be)
        {
            var storage = be.storage.inputStorage;
            return (int) Math.ceil(15f * ((double) storage.amount / storage.getCapacity()));
        }
        return super.getComparatorOutput(state, world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return GrinderBlock.createCuboidShape(0,0,0,16,15,16);

    }
}
