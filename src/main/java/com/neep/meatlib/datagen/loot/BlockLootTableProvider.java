package com.neep.meatlib.datagen.loot;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generate()
    {
        for (Map.Entry<Identifier, Block> entry : BlockRegistry.REGISTERED_BLOCKS.entrySet())
        {
            if (entry.getValue() instanceof MeatlibBlock meatBlock)
            {
                ItemConvertible like = meatBlock.dropsLike();
                if (meatBlock.autoGenDrop() && like != null)
                    this.addDrop(entry.getValue(), like);
            }
        }
    }
}
