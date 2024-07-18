package com.neep.neepmeat.datagen.tag;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class NMTags
{
    public static final TagKey<Item> CHARNEL_COMPACTOR = registerItem(NeepMeat.NAMESPACE, "charnel_substrate");
    public static final TagKey<Item> BLOOD_BUBBLE_LOGS = registerItem(NeepMeat.NAMESPACE, "blood_bubble_logs");
    public static final TagKey<Item> FLUID_PIPES = registerItem(NeepMeat.NAMESPACE, "fluid_pipes");
    public static final TagKey<Item> RAW_MEAT = TagKey.of(Registries.ITEM.getKey(), new Identifier("c", "raw_meat"));
    public static final TagKey<Item> RAW_FISH = TagKey.of(Registries.ITEM.getKey(), new Identifier("c", "raw_fish"));
    public static final TagKey<Item> METAL_SCAFFOLDING = TagKey.of(Registries.ITEM.getKey(), new Identifier("c", "metal_scaffolding"));
    public static final TagKey<Item> ROUGH_CONCRETE = TagKey.of(Registries.ITEM.getKey(), new Identifier("neepmeat", "rough_concrete"));
    public static final TagKey<Item> SMOOTH_TILE = TagKey.of(Registries.ITEM.getKey(), new Identifier("neepmeat", "smooth_tile"));
    public static final TagKey<Item> PAINTED_CORRUGATED_ASBESTOS = TagKey.of(Registries.ITEM.getKey(), new Identifier("neepmeat", "painted_corrugated_asbestos"));
    public static final TagKey<Item> GUIDE_LOOKUP = TagKey.of(Registries.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "guide_lookup"));

    public static final TagKey<Block> PHAGE_RAY_OVERRIDE = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "phage_ray_override_hardness"));

    public static final TagKey<Block> BLOCK_CRUSHING_INPUTS = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "block_crushing_inputs"));
    public static final TagKey<Item> BLOCK_CRUSHING_OUTPUTS = TagKey.of(Registries.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "block_crushing_outputs"));

    public static final TagKey<Block> CHARNEL_PUMP_OUTPUT_ORES = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "charnel_pump_output_ores"));
    public static final TagKey<Block> WRITHING_EARTH_REPLACABLE = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "writhing_earth_replacable"));
    public static final TagKey<Block> CONTAMINATED_RUBBLE_REPLACABLE = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "contaminated_rubble_replacable"));

    public static final TagKey<Block> ROCK_DRILL_MINEABLE = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "rock_drill_mineable"));

    public static final TagKey<EntityType<?>> CLONEABLE = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier(NeepMeat.NAMESPACE, "cloneable"));

    private static TagKey<Item> registerItem(String namespace, String id)
    {
        return TagKey.of(Registries.ITEM.getKey(), new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
