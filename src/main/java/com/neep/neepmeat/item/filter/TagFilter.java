package com.neep.neepmeat.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;

import java.util.HashSet;
import java.util.Set;

public class TagFilter implements Filter
{
    private final Set<TagKey<Item>> tags = new HashSet<>();

    @Override
    public boolean matches(ItemVariant variant)
    {
        return variant.getItem().getRegistryEntry().streamTags().anyMatch(tags::contains);
    }

    @Override
    public Constructor<?> getType()
    {
        return Filters.TAG;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
