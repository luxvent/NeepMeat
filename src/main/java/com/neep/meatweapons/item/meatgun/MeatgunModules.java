package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class MeatgunModules
{
    public static RegistryKey<Registry<MeatgunModule.Type<? extends MeatgunModule>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MeatWeapons.NAMESPACE, "meatgun_module"));
    public static final DefaultedRegistry<MeatgunModule.Type<? extends MeatgunModule>> REGISTRY = FabricRegistryBuilder.createDefaulted(REGISTRY_KEY, MeatgunModule.DEFAULT_TYPE.getId()).buildAndRegister();

    public static final MeatgunModule.Type<BaseModule> BASE = register(new Identifier(MeatWeapons.NAMESPACE, "base"), p -> new BaseModule(), BaseModule::fromNbt);
    public static final MeatgunModule.Type<PistolModule> PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "pistol"), p -> new PistolModule(), PistolModule::new);
    public static final MeatgunModule.Type<ChuggerModule> CHUGGER = register(new Identifier(MeatWeapons.NAMESPACE, "chugger"), p -> new ChuggerModule(), ChuggerModule::new);
    public static final MeatgunModule.Type<BosherModule> BOSHER = register(new Identifier(MeatWeapons.NAMESPACE, "bosher"), p -> new BosherModule(), BosherModule::new);
    public static final MeatgunModule.Type<LongBoiModule> LONG_BOI = register(new Identifier(MeatWeapons.NAMESPACE, "long_boi"), p -> new LongBoiModule(), LongBoiModule::new);
    public static final MeatgunModule.Type<TripleCarouselModule> TRIPLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "triple_carousel"), p -> new TripleCarouselModule(), TripleCarouselModule::new);
    public static final MeatgunModule.Type<DoubleCarouselModule> DOUBLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "double_carousel"), p -> new DoubleCarouselModule(), DoubleCarouselModule::new);
    public static final MeatgunModule.Type<UnderbarrelModule> UNDERBARREL = register(new Identifier(MeatWeapons.NAMESPACE, "underbarrel"), p -> new UnderbarrelModule(), UnderbarrelModule::new);

    public static final MeatgunModule.Type<BatteryModule> BATTERY = register(new Identifier(MeatWeapons.NAMESPACE, "battery"), p -> new BatteryModule(), BatteryModule::new);

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, MeatgunModule.Factory<T> factory, MeatgunModule.NbtFactory<T> nbtFactory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, factory, nbtFactory));
    }

    static
    {
        Registry.register(REGISTRY, REGISTRY.getDefaultId(), MeatgunModule.DEFAULT_TYPE);
    }
}
