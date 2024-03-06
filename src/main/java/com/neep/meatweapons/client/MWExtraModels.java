package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class MWExtraModels implements ExtraModelProvider
{
    public static MWExtraModels EXTRA_MODELS = new MWExtraModels();

    public static Identifier MEATGUN_BASE = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/base_module");
    public static Identifier MEATGUN_BOSHER = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/bosher");

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {
        out.accept(MEATGUN_BASE);
        out.accept(MEATGUN_BOSHER);
    }
}
