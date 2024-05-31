package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.small_trommel.TrommelRecipe;
import com.neep.neepmeat.machine.small_trommel.TrommelStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@SuppressWarnings("UnstableApiUsage")
public class FatTrommelRecipe implements TrommelRecipe
{
    protected final Identifier id;
    private final Random random = Random.create();

    public FatTrommelRecipe(Identifier id)
    {
        this.id = id;
    }

    @Override
    public boolean matches(TrommelStorage inventory)
    {
        SingleVariantStorage<FluidVariant> storage = inventory.input();
        return OreFatRegistry.getFromVariant(storage.getResource()) != null;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.FAT_TROMMEL;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.FAT_TROMMEL_SERIALIZER;
    }

    @Override
    public boolean takeInputs(TrommelStorage storage, TransactionContext transaction)
    {
        return true;
    }

    @Override
    public boolean ejectOutputs(TrommelStorage context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            SingleVariantStorage<FluidVariant> fluidStorage = context.input();

            FluidVariant resource = fluidStorage.getResource();
//            OreFatRegistry.Entry entry = OreFatRegistry.getFromVariant(resource);

            long ex2 = fluidStorage.extract(fluidStorage.getResource(), INPUT_AMOUNT, inner);
            if (ex2 != INPUT_AMOUNT)
            {
                inner.abort();
                return false;
            }

            boolean produceExtra = random.nextFloat() > 0.5;
            long outputAmount = INPUT_AMOUNT * (produceExtra ? 2 : 1);

            FluidVariant outputVariant = OreFatRegistry.getClean(OreFatRegistry.getItem(resource));
            long inserted = context.output().insert(outputVariant, outputAmount, inner);

            if (inserted == outputAmount)
            {
                inner.commit();
                return true;
            }
        }
        return false;
    }

    public static class Serializer implements MeatRecipeSerialiser<FatTrommelRecipe>
    {

        public Serializer()
        {
        }

        @Override
        public FatTrommelRecipe read(Identifier id, JsonObject json)
        {
            return new FatTrommelRecipe(id);
        }

        @Override
        public FatTrommelRecipe read(Identifier id, PacketByteBuf buf)
        {
            return new FatTrommelRecipe(id);
        }

        @Override
        public void write(PacketByteBuf buf, FatTrommelRecipe recipe)
        {
        }
    }
}