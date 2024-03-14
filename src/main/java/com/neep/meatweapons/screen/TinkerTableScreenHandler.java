package com.neep.meatweapons.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.Receiver;
import com.neep.meatlib.network.ServerChannelReceiver;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.init.MWScreenHandlers;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TinkerTableScreenHandler extends BasicScreenHandler
{
    public static final int BACKGROUND_WIDTH = 340;
    public static final int BACKGROUND_HEIGHT = 200;

    public static final Identifier CHANNEL_ID = new Identifier(MeatWeapons.NAMESPACE, "chunnel");
    public static final ChannelFormat<Thing> CHANNEL_FORMAT = ChannelFormat.builder(Thing.class)
            .param(ParamCodec.INT)
            .param(ParamCodec.INT)
            .build();

    private Receiver<Thing> receiver = Receiver.empty();

    public TinkerTableScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public TinkerTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockInv)
    {
        super(MWScreenHandlers.MEATGUN, playerInventory, blockInv, syncId, null);

        addSlot(new Slot(blockInv, 0, 4, BACKGROUND_HEIGHT - 2 - 17));
        createHotbar(5 + 18, BACKGROUND_HEIGHT - 19, playerInventory); // Yay! I love hardcoding!

        if (playerInventory.player instanceof ServerPlayerEntity serverPlayerEntity)
            this.receiver = new ServerChannelReceiver<>(serverPlayerEntity, CHANNEL_ID, CHANNEL_FORMAT, this::thing);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        receiver.close();
    }

    public void thing(int i, int j)
    {
//        System.out.println(i);
//        System.out.println(j);
    }

    public interface Thing
    {
        void apply(int i, int j);
    }
}
