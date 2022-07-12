package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class NMRecipes
{
    public static void init()
    {

    }

    protected static void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.RUSTY_METAL_BLOCK, Items.WATER_BUCKET, NMBlocks.POLISHED_METAL);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.DIRTY_RED_TILES, NMFluids.BLOOD_BUCKET, NMBlocks.YELLOW_TILES);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.BLUE_IRON_BLOCK, Items.BLUE_DYE, NMBlocks.POLISHED_METAL);
//        MeatRecipeProvider.offerEightDyeingRecipe(exporter, NMBlocks.DIRTY_YELLOW_TILES, NMFluids.BLOOD_BUCKET, NMBlocks.YELLOW_TILES);
    }

    static
    {
        MeatRecipeProvider.addSubsidiary(NMRecipes::generateRecipes);
    }
}