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
    public static Identifier BOSHER = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/bosher");
    public static Identifier MEATGUN_PISTOL = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/pistol");
    public static Identifier MEATGUN_CHUGGER = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/chugger");
    public static Identifier MEATGUN_TRIPLE_CAROUSEL = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/triple_carousel");
    public static Identifier MEATGUN_DOUBLE_CAROUSEL = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/double_carousel");
    public static Identifier BATTERY = new Identifier(MeatWeapons.NAMESPACE, "item/meatgun/battery");

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {
        out.accept(MEATGUN_BASE);
        out.accept(MEATGUN_PISTOL);
        out.accept(BOSHER);
        out.accept(MEATGUN_CHUGGER);
        out.accept(MEATGUN_TRIPLE_CAROUSEL);
        out.accept(MEATGUN_DOUBLE_CAROUSEL);
        out.accept(BATTERY);
    }
}