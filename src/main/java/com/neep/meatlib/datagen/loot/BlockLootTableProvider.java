package com.neep.meatlib.datagen.loot;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generate()
    {
        for (Block entry : BlockRegistry.REGISTERED_BLOCKS)
        {
            if (entry instanceof MeatlibBlock meatBlock)
            {
                if (meatBlock.autoGenDrop())
                {
                    var builder = meatBlock.genLoot(this);
                    if (builder != null)
                    {
                        addDrop(entry, builder);
                    }
                    else
                    {
                        // Legacy behaviour
                        ItemConvertible like = meatBlock.dropsLike();
                        if (like != null)
                        {
                            this.addDrop(entry, like);
                        }
                    }
                }
            }
            else if (entry instanceof MeatlibBlockExtension extension)
            {
                // Allow specifying drops using settings
                ItemConvertible drop = extension.neepmeat$simpleDrop();
                if (drop != null)
                {
                    this.addDrop(entry, drop);
                }
            }
        }
    }
}
