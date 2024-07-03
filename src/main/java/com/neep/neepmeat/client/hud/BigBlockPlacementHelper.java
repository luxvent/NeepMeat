package com.neep.neepmeat.client.hud;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.plc.PLCCols;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import static com.neep.neepmeat.client.plc.PLCHudRenderer.drawCuboidShapeOutline;

public class BigBlockPlacementHelper
{
    @Nullable
    private static Result RESULT;

    public static void init()
    {
        WorldRenderEvents.BLOCK_OUTLINE.register(BigBlockPlacementHelper::onRender);
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (client.world != null && client.player != null)
                tick(client);
        });
    }

    private static void tick(MinecraftClient client)
    {
        ItemStack mainStack = client.player.getMainHandStack();
        if (mainStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof BigBlock<?> bigBlock
                && client.crosshairTarget instanceof BlockHitResult hitResult)
        {
            BlockState placementState;

            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(client.player, client.player.preferredHand, mainStack, hitResult);
            BlockPos pos = itemPlacementContext.getBlockPos();
            boolean place = itemPlacementContext.canPlace();

            if (MeatLib.vsUtil != null && MeatLib.vsUtil.hasShipAtPosition(hitResult.getBlockPos(), client.world))
            {
                placementState = MeatLib.vsUtil.transformPlayerTemporarily(client.player, client.world, hitResult.getBlockPos(), () ->
                        bigBlock.getPlacementState(itemPlacementContext));
            }
            else
            {
                placementState = bigBlock.getPlacementState(itemPlacementContext);
            }

            if (placementState != null)
            {
                place = place && bigBlock.canPlaceAt(placementState, client.world, pos);

                RESULT = new Result(
                        bigBlock.getVolume(placementState).toVoxelShape(),
                        pos,
                        place ? PLCCols.BORDER.col : PLCCols.ERROR_LINE.col);
            }

            return;
        }
        RESULT = null;
    }

    private static boolean onRender(WorldRenderContext wrctx, WorldRenderContext.BlockOutlineContext blockOutlineContext)
    {
        if (RESULT != null)
        {
            MinecraftClient client = MinecraftClient.getInstance();
            Vec3d camPos = client.gameRenderer.getCamera().getPos();
            BlockPos pos = RESULT.pos();

            MatrixStack matrices = wrctx.matrixStack();

            matrices.push();

            matrices.translate(-camPos.x, -camPos.y, -camPos.z);

            if (MeatLib.vsUtil != null)
                MeatLib.vsUtil.CLIENT.transformRenderIfOnShip(matrices, new Vector3d(pos.getX(), pos.getY(), pos.getZ()));

            int col = RESULT.col();

            drawCuboidShapeOutline(
                    wrctx.matrixStack(),
                    wrctx.consumers().getBuffer(RenderLayer.getLines()),
                    RESULT.shape(),
                    0,
                    0,
                    0,
                    ((col >> 16) & 0xff) / 255f, ((col >> 8) & 0xff) / 255f, ((col >> 0) & 0xff) / 255f, 1
            );

            matrices.pop();

            return false;
        }
        return true;
    }

    private static record Result(VoxelShape shape, BlockPos pos, int col)
    {

    }
}
