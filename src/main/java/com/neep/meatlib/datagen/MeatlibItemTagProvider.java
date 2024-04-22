package com.neep.meatlib.datagen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MeatlibItemTagProvider extends FabricTagProvider.ItemTagProvider
{
//    private static final Set<FabricDataGenerator.Pack.RegistryDependentFactory<? extends ItemTagProvider>> SUBSIDIARIES = Sets.newHashSet();
//    private final Set<ItemTagProvider> subsidiaries = Sets.newHashSet();

    public MeatlibItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);

//        for (var factory : SUBSIDIARIES)
//        {
//            subsidiaries.add(factory.create(output, registriesFuture));
//        }
    }

//    public static <T extends ItemTagProvider> void register(FabricDataGenerator.Pack.RegistryDependentFactory<T> factory)
//    {
//        SUBSIDIARIES.add(factory);
//    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        Multimap<TagKey<Item>, Item> map = HashMultimap.create();
        ItemRegistry.REGISTERED_ITEMS.stream()
                .filter(item -> item instanceof MeatlibItem)
                .forEach(item -> ((MeatlibItem) item).appendTags(tag -> map.put(tag, item)));

        map.asMap().forEach((tag, items) ->
        {
            var builder = getOrCreateTagBuilder(tag);
            items.forEach(builder::add);
        });
    }
}
