package com.neep.meatweapons.init;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatweapons.block.TinkerTableBlock;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;

public class MWBlocks
{
    public static final Block TINKER_TABLE = BlockRegistry.queue(new TinkerTableBlock("tinker_table",ItemSettings.block(), FabricBlockSettings.copyOf(NMBlocks.RUSTY_METAL_BLOCK)));

    public static void init()
    {

    }
}
