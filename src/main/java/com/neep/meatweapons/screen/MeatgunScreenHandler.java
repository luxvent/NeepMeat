package com.neep.meatweapons.screen;

import com.neep.meatweapons.init.MWScreenHandlers;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MeatgunScreenHandler extends BasicScreenHandler
{
    public static final int BACKGROUND_WIDTH = 340;
    public static final int BACKGROUND_HEIGHT = 200;

    public MeatgunScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public MeatgunScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockInv)
    {
        super(MWScreenHandlers.MEATGUN, playerInventory, blockInv, syncId, null);

        addSlot(new Slot(blockInv, 0, 4, BACKGROUND_HEIGHT - 2 - 17));
        createHotbar(5 + 18, BACKGROUND_HEIGHT - 19, playerInventory); // Yay! I love hardcoding!
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
}
