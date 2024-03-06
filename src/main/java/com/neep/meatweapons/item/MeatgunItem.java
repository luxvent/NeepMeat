package com.neep.meatweapons.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.renderer.MeatgunRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MeatgunItem extends BaseItem implements GeoItem
{
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public MeatgunItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private MeatgunRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer()
            {
                if (renderer == null)
                    renderer = new MeatgunRenderer();

                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }
}
