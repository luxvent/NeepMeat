package com.neep.meatlib.item;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Consumer;

public interface MeatlibItem
{
    String getRegistryName();

    default void appendTags(Consumer<TagKey<Item>> consumer)
    {
    }
}
