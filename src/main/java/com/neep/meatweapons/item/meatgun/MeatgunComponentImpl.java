package com.neep.meatweapons.item.meatgun;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    public MeatgunComponentImpl(ItemStack stack)
    {
        super(stack);
    }

    private final BaseModule root = new BaseModule();

    @Override
    public MeatgunModule getRoot()
    {
        return root;
    }
}
