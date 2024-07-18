package com.neep.neepmeat.api.processing;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neep.meatlib.api.event.DataPackPostProcess;
import com.neep.meatlib.mixin.RecipeManagerAccessor;
import com.neep.neepmeat.BalanceConstants;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRegistry implements SimpleSynchronousResourceReloadListener
{
    public static final Identifier SYNC_ID = new Identifier(NeepMeat.NAMESPACE, "ore_fat");

    public static final OreFatRegistry INSTANCE = new OreFatRegistry();

    private final Set<TagKey<Item>> generateForTags = Sets.newHashSet();
    private final Set<Identifier> generateForItems = Sets.newHashSet();

    private final Map<Item, Entry> outputToEntry = Maps.newHashMap();
    private final Map<Item, Entry> inputToEntry = Maps.newHashMap();
    private final Map<NbtCompound, Entry> nbtToEntry = Maps.newHashMap();

    public static void init()
    {
        DataPackPostProcess.AFTER_DATA_PACK_LOAD.register(INSTANCE::generate);
        DataPackPostProcess.SYNC.register(INSTANCE::sync);
    }

    @Nullable
    public static Entry get(NbtCompound nbt)
    {
        return INSTANCE.nbtToEntry.get(nbt);
    }

    @Nullable
    public static Entry getFromInput(Item item)
    {
        return INSTANCE.inputToEntry.get(item);
    }

    @Nullable
    public static Entry getFromVariant(FluidVariant variant)
    {
        if (variant.getObject() instanceof OreFatFluidFactory.Main)
        {
            NbtCompound nbt = variant.getNbt();
            return INSTANCE.nbtToEntry.get(nbt);
        }
        return null;
    }

    private void addTag(Identifier id)
    {
        generateForTags.add(TagKey.of(Registries.ITEM.getKey(), id));
    }

    private void generate(MinecraftServer server)
    {
        RecipeManagerAccessor manager = (RecipeManagerAccessor) server.getRecipeManager();
        Map<Identifier, SmeltingRecipe> smeltingRecipes = manager.callGetAllOfType(RecipeType.SMELTING);
        Map<Identifier, BlastingRecipe> blastingRecipes = manager.callGetAllOfType(RecipeType.BLASTING);
        Streams.concat(smeltingRecipes.values().stream(), blastingRecipes.values().stream()).forEach(recipe ->
        {
            List<Ingredient> ingredients = recipe.getIngredients();
            ItemStack output = recipe.getOutput(server.getRegistryManager());
            INSTANCE.testIngredient(ingredients, output);
        });

        NeepMeat.LOGGER.info("Generated {} ore fat routes", inputToEntry.size());
    }

    private void sync(MinecraftServer server, Set<ServerPlayerEntity> players)
    {
        OreFatSyncS2CPacket packet = new OreFatSyncS2CPacket(nbtToEntry);
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        for (ServerPlayerEntity player : players)
        {
            ServerPlayNetworking.send(player, SYNC_ID, buf);
        }
    }

    public void onPacket(OreFatSyncS2CPacket t)
    {
        nbtToEntry.clear();
        nbtToEntry.putAll(t.nbtToEntry());
    }

    /**
     * Runs through the inputs and checks if they are raw ores.
     * If this is the case, it is probably safe to generate an ore fat path from input to output.
     */
    private void testIngredient(Collection<Ingredient> input, ItemStack output)
    {
        if (output.isEmpty())
            return;

        if (input.isEmpty())
            return;

        for (Ingredient ingredient : input)
        {
            if (ingredient.isEmpty())
                continue;

            for (ItemStack stack : ingredient.getMatchingStacks())
            {
                Identifier itemId = stack.getItem().getRegistryEntry().registryKey().getValue();
                if (stack.streamTags().anyMatch(generateForTags::contains) || generateForItems.contains(itemId))
                {
                    registerDefaultEntry(stack.getItem(), ItemVariant.of(output));
                }
            }
        }
    }

    private void registerDefaultEntry(Item input, ItemVariant output)
    {
        NbtCompound nbt = createNbt(output.getItem());

        Entry entry = outputToEntry.computeIfAbsent(output.getItem(), i -> new Entry(
                createName(output.getItem(), NMFluids.DIRTY_ORE_FAT),
                createName(output.getItem(), NMFluids.DIRTY_ORE_FAT),
                output, nbt, BalanceConstants.DEFAULT_ORE_RENDERING_YIELD, BalanceConstants.DEFAULT_ORE_TROMMEL_YIELD));
        inputToEntry.put(input, entry);
        nbtToEntry.put(nbt, entry);
    }

    /**
     * @param outputItem Output item
     * @return The NBT compound that held by all dirty and clean FluidVariants corresponding to the item.
     */
    private NbtCompound createNbt(Item outputItem)
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("item", Registries.ITEM.getId(outputItem).toString());
        return nbtCompound;
    }

    private Text createName(Item outputItem, Block fluid)
    {
        return Text.translatable(fluid.getTranslationKey(), outputItem.getName());
    }

    public Item getItem(FluidVariant variant)
    {
        NbtCompound nbt = variant.getNbt();
        if (variant.getObject() instanceof OreFatFluidFactory.Main && nbt != null)
        {
            return Registries.ITEM.get(new Identifier((nbt.getString("item"))));
        }
        return null;
    }

    public FluidVariant getDirty(Item ore)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item", Registries.ITEM.getId(ore).toString());
        return FluidVariant.of(NMFluids.STILL_DIRTY_ORE_FAT, nbt);
    }

    @Override
    public Identifier getFabricId()
    {
        return new Identifier(NeepMeat.NAMESPACE, "ore_fat");
    }

    /**
     * This works in a very convoluted way.
     * Fats have one output and multiple inputs. Therefore, fats can be identified by their output item.
     * First the main fat file is processed. This determines which tags will be used to generate fat fluids from
     * smelting recipes.
     * Overrides are then parsed. These pre-populate the maps with some. entries
     * The generation step occurs after data packs have been reloaded. All smelting recipes are checked for inputs
     * and outputs that match the tags in fat.json.
     * Entries that already exist for a given output will not be replaced by generated entries.
     */
    @Override
    public void reload(ResourceManager manager)
    {
        inputToEntry.clear();
        nbtToEntry.clear();
        outputToEntry.clear();

        for (Identifier id : manager.findResources("ore_fat", path -> path.getPath().endsWith("fat.json")).keySet())
        {
            if (manager.getResource(id).isPresent())
            {
                try (InputStream stream = manager.getResource(id).get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);
                    JsonObject rootObject = (JsonObject) rootElement;

                    if (rootObject.has("replace") && JsonHelper.getBoolean(rootObject, "replace"))
                    {
                        // Last file replaces previous ones
                        generateForTags.clear();
                        generateForItems.clear();
                        ;
                    }

                    // Generate routes for smelting recipes whose inputs are in these tags
                    JsonArray generateForTags = JsonHelper.getArray(rootObject, "generate_for_tags");
                    generateForTags.forEach(e ->
                    {
                        Identifier tagId = Identifier.tryParse(e.getAsString());
                        addTag(tagId);
                    });

                    JsonArray generateForItems = JsonHelper.getArray(rootObject, "generate_for_items");
                    generateForItems.forEach(e ->
                    {
                        Identifier itemId = Identifier.tryParse(e.getAsString());

                        this.generateForItems.add(itemId);
                    });
                }
                catch (Exception e)
                {
                    NeepMeat.LOGGER.error("Error while reading ore fat " + id.toString(), e);
                }
            }
        }

        readOverrides(manager);
    }

    private void readOverrides(ResourceManager manager)
    {
        for (Identifier id : manager.findResources("ore_fat/overrides", path -> path.getPath().endsWith(".json")).keySet())
        {
            if (manager.getResource(id).isPresent())
            {
                try (InputStream stream = manager.getResource(id).get().getInputStream())
                {
                    Reader reader = new InputStreamReader(stream);
                    JsonElement rootElement = JsonParser.parseReader(reader);
                    JsonObject rootObject = (JsonObject) rootElement;

                    String resourceName = JsonHelper.getString(rootObject, "output");
                    Item result = Registries.ITEM.get(Identifier.tryParse(resourceName));

                    Text dirtyFatName = createName(result, NMFluids.DIRTY_ORE_FAT);
                    if (rootObject.has("dirty_fat_name"))
                        dirtyFatName = Text.translatable(JsonHelper.getString(rootObject, "dirty_fat_name"));

                    Text cleanFatName = createName(result, NMFluids.CLEAN_ORE_FAT);
                    if (rootObject.has("clean_fat_name"))
                        cleanFatName = Text.translatable(JsonHelper.getString(rootObject, "clean_fat_name"));

                    float renderingYield = BalanceConstants.DEFAULT_ORE_RENDERING_YIELD;
                    if (rootObject.has("rendering_yield"))
                        renderingYield = JsonHelper.getFloat(rootObject, "rendering_yield");

                    float trommelYield = BalanceConstants.DEFAULT_ORE_TROMMEL_YIELD;
                    if (rootObject.has("trommel_yield"))
                        trommelYield = JsonHelper.getFloat(rootObject, "trommel_yield");

                    Entry entry = new Entry(dirtyFatName, cleanFatName, ItemVariant.of(result), createNbt(result), renderingYield, trommelYield);
                    outputToEntry.put(result, entry);
                    nbtToEntry.put(createNbt(result), entry);

                    if (rootObject.has("inputs"))
                    {
                        JsonArray inputs = JsonHelper.getArray(rootObject, "inputs");
                        for (var inputObject : inputs)
                        {
                            String inputName = inputObject.getAsString();
                            Item input = Registries.ITEM.get(Identifier.tryParse(inputName));
                            inputToEntry.put(input, entry);
                        }
                    }
                }
                catch (Exception e)
                {
                    NeepMeat.LOGGER.error("Error while reading ore fat override " + id.toString(), e);
                }
            }
        }
    }

    public record Entry(Text dirtyFatName, Text cleanFatName, ItemVariant result, NbtCompound nbt, float renderingYield,
                        float trommelYield)
    {
//        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
//                instance.group(
//                        Codecs.TEXT.fieldOf("dirty_fat_name").forGetter(Entry::dirtyFatName),
//                        Codecs.TEXT.fieldOf("clean_fat_name").forGetter(Entry::cleanFatName),
//                        MeatlibStorageUtil.ITEM_VARIANT_CODEC.fieldOf("result").forGetter(Entry::result),
//                        NbtCompound.CODEC.fieldOf("nbt").forGetter(Entry::nbt),
//                        Codec.FLOAT.fieldOf("rendering_yield").forGetter(Entry::renderingYield),
//                        Codec.FLOAT.fieldOf("trommel_yield").forGetter(Entry::trommelYield)
//                ).apply(instance, Entry::new));

        public static Entry read(PacketByteBuf buf)
        {
            return new Entry(
                    Text.Serializer.fromJson(buf.readString()),
                    Text.Serializer.fromJson(buf.readString()),
                    ItemVariant.fromPacket(buf),
                    buf.readNbt(),
                    buf.readFloat(),
                    buf.readFloat()
            );
        }

        public void write(PacketByteBuf buf)
        {
            buf.writeString(Text.Serializer.toJson(dirtyFatName));
            buf.writeString(Text.Serializer.toJson(cleanFatName));
            result.toPacket(buf);
            buf.writeNbt(nbt);
            buf.writeFloat(renderingYield);
            buf.writeFloat(trommelYield);
        }

        public FluidVariant getDirty()
        {
            return FluidVariant.of(NMFluids.STILL_DIRTY_ORE_FAT, nbt);
        }

        public FluidVariant getClean()
        {
            return FluidVariant.of(NMFluids.STILL_CLEAN_ORE_FAT, nbt);
        }
    }
}
