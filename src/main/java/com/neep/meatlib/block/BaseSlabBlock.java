package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import org.jetbrains.annotations.Nullable;

public class BaseSlabBlock extends SlabBlock implements MeatlibBlock
{
    protected String registryName;
    protected BlockItem blockItem;

    public BaseSlabBlock(BlockState baseBlockState, String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public LootTable.@Nullable Builder genLoot(BlockLootTableGenerator generator)
    {
        return generator.slabDrops(this);
    }
}
