package com.neep.meatweapons.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;

import java.util.HashSet;
import java.util.Set;

class TagFilter implements Filter
{
    private final Set<TagKey<Item>> tags = new HashSet<>();

    @Override
    public boolean matches(ItemVariant variant, long amount)
    {
        return variant.getItem().getRegistryEntry().streamTags().anyMatch(tags::contains);
    }
}
