package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMCommonPlugin;
import com.neep.neepmeat.recipe.BlockCrushingRecipe;
import com.neep.neepmeat.recipe.CrushingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class GrindingDisplay extends BasicDisplay
{
    private final CategoryIdentifier<?> categoryIdentifier;
    private CrushingRecipe recipe;

    public static boolean isValid(CrushingRecipe recipe)
    {
        return !recipe.destroy() && !(recipe instanceof BlockCrushingRecipe);
    }

    public GrindingDisplay(CategoryIdentifier<?> categoryIdentifier, CrushingRecipe recipe)
    {
        // CURSED
        this(categoryIdentifier,
                Util.make(() ->
                {
                    List<EntryIngredient> list = new ArrayList<>();
                    list.add(NMCommonPlugin.inputToIngredient(recipe.getItemInput()));
                    return list;
                }),
                Util.make(() ->
                {
                    List<EntryIngredient> list = new ArrayList<>();
                    list.add(EntryIngredients.ofItems(List.of(recipe.getItemOutput().resource()), (int) recipe.getItemOutput().minAmount()));

                    list.get(0).forEach(stack -> stack.tooltip(List.of(
                            Text.of("Min: " + recipe.getItemOutput().minAmount() + ", Max: " + recipe.getItemOutput().maxAmount()))));

                    if (recipe.getAuxOutput() != null)
                    {
                        list.add(EntryIngredients.ofItems(List.of(recipe.getAuxOutput().resource()), (int) recipe.getAuxOutput().minAmount()));
                        list.get(1).forEach(stack -> stack.tooltip(List.of(
                                Text.of("Chance: " + recipe.getAuxOutput().chance())
                        )));
                    }
                    return list;
                }),
                Optional.empty()
        );

        this.recipe = recipe;
    }

    public GrindingDisplay(CategoryIdentifier<?> categoryIdentifier, List<EntryIngredient> input, List<EntryIngredient> output, Optional<Identifier> location)
    {
        super(input, output);
        this.categoryIdentifier = categoryIdentifier;
    }

    public static BasicDisplay.Serializer<GrindingDisplay> serializer(CategoryIdentifier<?> categoryIdentifier)
    {
        return BasicDisplay.Serializer.ofSimple((input, output, location1) -> new GrindingDisplay(categoryIdentifier, input, output, location1));
    }

    public static <T extends CrushingRecipe> Function<T, GrindingDisplay> filler(CategoryIdentifier<? extends GrindingDisplay> categoryIdentifier)
    {
        return r -> new GrindingDisplay(categoryIdentifier, r);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return categoryIdentifier;
    }

}
