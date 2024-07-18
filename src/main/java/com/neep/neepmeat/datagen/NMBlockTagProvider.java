package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class NMBlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public NMBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    public static void init()
    {
        MeatLibDataGen.register(NMBlockTagProvider::new);
    }

    @Override
    public String getName()
    {
        return "Tags for " + this.registryRef.getValue() + " (" + NeepMeat.NAMESPACE + ")";
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        // Blocks whose corresponding items will be used as recipe inputs for crushing if their loot table
        // contains a raw ore.
        getOrCreateTagBuilder(NMTags.BLOCK_CRUSHING_INPUTS)
                .addOptionalTag(ConventionalBlockTags.ORES)
        ;

        getOrCreateTagBuilder(NMTags.CHARNEL_PUMP_OUTPUT_ORES)
                .addOptionalTag(ConventionalBlockTags.ORES);

        getOrCreateTagBuilder(NMTags.WRITHING_EARTH_REPLACABLE)
                .addOptionalTag(BlockTags.STONE_ORE_REPLACEABLES)
                .addOptionalTag(BlockTags.NETHER_CARVER_REPLACEABLES)
                .addOptionalTag(BlockTags.LUSH_GROUND_REPLACEABLE)
                .addOptionalTag(BlockTags.LUSH_GROUND_REPLACEABLE)
                .addOptionalTag(BlockTags.SNOW)
                .addOptionalTag(BlockTags.SHOVEL_MINEABLE)
                .addOptionalTag(BlockTags.DIRT)
        ;

        getOrCreateTagBuilder(NMTags.CONTAMINATED_RUBBLE_REPLACABLE)
                .addOptionalTag(BlockTags.SNOW)
                .addOptionalTag(BlockTags.LUSH_GROUND_REPLACEABLE)
                .addOptionalTag(BlockTags.SHOVEL_MINEABLE)
                .addOptionalTag(BlockTags.DIRT)
                .addOptionalTag(BlockTags.NETHER_CARVER_REPLACEABLES)
        ;

        getOrCreateTagBuilder(NMTags.ROCK_DRILL_MINEABLE)
                .addOptionalTag(BlockTags.STONE_ORE_REPLACEABLES)
                .addOptionalTag(BlockTags.NETHER_CARVER_REPLACEABLES)
                .addOptionalTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
        ;
    }
}
