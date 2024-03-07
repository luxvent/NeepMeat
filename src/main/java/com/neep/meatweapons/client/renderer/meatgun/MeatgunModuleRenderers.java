package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.item.meatgun.MeatgunModule;
import com.neep.meatweapons.item.meatgun.MeatgunModules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MeatgunModuleRenderers
{
    private static final Map<MeatgunModule.Type<?>, MeatgunModuleRenderer.Factory> RENDERER_FACTORIES = new HashMap<>();
    private static final Map<MeatgunModule.Type<? extends MeatgunModule>, MeatgunModuleRenderer<?>> RENDERERS = new HashMap<>();

    public static void register(MeatgunModule.Type<?> type, MeatgunModuleRenderer.Factory factory)
    {
        RENDERER_FACTORIES.put(type, factory);
    }

    public static <T extends MeatgunModule> MeatgunModuleRenderer<T> get(T module)
    {
        return (MeatgunModuleRenderer<T>)
                RENDERERS.computeIfAbsent(module.getType(), id -> RENDERER_FACTORIES.get(module.getType()).create(MinecraftClient.getInstance()));
    }

    public static void init()
    {
        register(MeatgunModules.BASE, BaseModuleRenderer::new);
        register(MeatgunModules.CHUGGER, ChuggerModuleRenderer::new);
    }
}
