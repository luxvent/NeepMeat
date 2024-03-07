package com.neep.meatweapons.init;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunComponentImpl;
import com.neep.neepmeat.NeepMeat;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MWComponents implements ItemComponentInitializer
{
    public static final ComponentKey<MeatgunComponent> MEATGUN =
            ComponentRegistry.getOrCreate(
                    new Identifier(MeatWeapons.NAMESPACE, "meatgun"),
                    MeatgunComponent.class);

    @Override
    public void registerItemComponentFactories(@NotNull ItemComponentFactoryRegistry registry)
    {
        registry.register(MWItems.MEATGUN, MEATGUN, MeatgunComponentImpl::new);
    }
}
