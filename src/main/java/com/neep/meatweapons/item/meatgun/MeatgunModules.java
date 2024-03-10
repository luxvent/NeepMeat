package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class MeatgunModules
{
    public static RegistryKey<Registry<MeatgunModule.Type<? extends MeatgunModule>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MeatWeapons.NAMESPACE, "meatgun_module"));
    public static final Registry<MeatgunModule.Type<? extends MeatgunModule>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final MeatgunModule.Type<BaseModule> BASE = register(new Identifier(MeatWeapons.NAMESPACE, "base"), p -> new BaseModule());
    public static final MeatgunModule.Type<PistolModule> PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "pistol"), p -> new PistolModule());
    public static final MeatgunModule.Type<ChuggerModule> CHUGGER = register(new Identifier(MeatWeapons.NAMESPACE, "chugger"), p -> new ChuggerModule());
    public static final MeatgunModule.Type<BosherModule> BOSHER = register(new Identifier(MeatWeapons.NAMESPACE, "bosher"), p -> new BosherModule());
    public static final MeatgunModule.Type<LongBoiModule> LONG_BOI = register(new Identifier(MeatWeapons.NAMESPACE, "long_boi"), p -> new LongBoiModule());
    public static final MeatgunModule.Type<TripleCarouselModule> TRIPLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "triple_carousel"), p -> new TripleCarouselModule());
    public static final MeatgunModule.Type<DoubleCarouselModule> DOUBLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "double_carousel"), p -> new DoubleCarouselModule());

    public static final MeatgunModule.Type<BatteryModule> BATTERY = register(new Identifier(MeatWeapons.NAMESPACE, "battery"), p -> new BatteryModule());

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, MeatgunModule.Factory<T> factory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, factory));
    }
}
