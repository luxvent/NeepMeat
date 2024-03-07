package com.neep.meatweapons.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.renderer.MeatgunRenderer;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MeatgunItem extends BaseItem implements GeoItem, WeakTwoHanded, GunItem
{
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    private final Random random = new Random();

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
    public boolean displayArmFirstPerson(ItemStack stack, Hand hand)
    {
        return true;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        MWComponents.MEATGUN.get(stack).trigger(world, player, stack, id, pitch, yaw, handType);
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

    @Override
    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        return Vec3d.ZERO;
    }

    @Override
    public void playSound(World world, LivingEntity entity, GunSounds sound)
    {

    }

    @Override
    public void syncAnimation(World world, LivingEntity player, ItemStack stack, String animation, boolean broadcast)
    {

    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public int getShots(ItemStack stack, int trigger)
    {
        return 0;
    }
}
