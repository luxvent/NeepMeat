package com.neep.meatweapons.item.filter;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;

import java.util.HashSet;
import java.util.Set;

public class FilterItem extends BaseItem
{
    public FilterItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    static class FilterComponent implements Filter, Component
    {
        private final Filter filter;

        public FilterComponent(Filter filter)
        {
            this.filter = filter;
        }

        @Override
        public boolean matches(ItemVariant variant, long amount)
        {
            return filter.matches(variant, amount);
        }

        @Override
        public void readFromNbt(NbtCompound tag)
        {

        }

        @Override
        public void writeToNbt(NbtCompound tag)
        {

        }
    }

}
