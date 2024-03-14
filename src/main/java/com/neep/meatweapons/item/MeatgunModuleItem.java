package com.neep.meatweapons.item;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.item.meatgun.MeatgunModule;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeatgunModuleItem extends Item
{
    private static final Map<MeatgunModuleItem, MeatgunModule.Type<?>> ITEM_TO_TYPE = new HashMap<>();
    private static final Map<MeatgunModule.Type<?>, MeatgunModuleItem> TYPE_TO_ITEM = new HashMap<>();
    @Nullable private final TooltipSupplier tooltipSupplier;

    public static void register(MeatgunModuleItem item, MeatgunModule.Type<?> type)
    {
        if (TYPE_TO_ITEM.containsKey(type))
            throw new IllegalArgumentException("Module type '" + type + "' is already bound to item '" + TYPE_TO_ITEM.get(type) + "'");

        ITEM_TO_TYPE.put(item, type);
        TYPE_TO_ITEM.put(type, item);
    }

    public static ItemStack get(MeatgunModule.Type<?> type)
    {
        MeatgunModuleItem item = TYPE_TO_ITEM.get(type);
        if (item != null)
        {
            return item.getDefaultStack();
        }
        return ItemStack.EMPTY;
    }

    public static MeatgunModule.Type<?> get(ItemStack stack)
    {
        if (stack.getItem() instanceof MeatgunModuleItem moduleItem)
        {
            MeatgunModule.Type<?> type = ITEM_TO_TYPE.get(moduleItem);
            if (type != null)
            {
                return type;
            }
        }
        return MeatgunModule.DEFAULT_TYPE;
    }

    public MeatgunModuleItem(MeatgunModule.Type<?> type, @Nullable TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(settings);
        this.tooltipSupplier = tooltipSupplier;
        register(this, type);
    }

    public MeatgunModuleItem(MeatgunModule.Type<?> type, Settings settings)
    {
        this(type, null, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        super.appendTooltip(stack, world, tooltip, context);
    }
}