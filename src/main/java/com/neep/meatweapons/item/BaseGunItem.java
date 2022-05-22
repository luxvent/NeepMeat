package com.neep.meatweapons.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Predicate;

public abstract class BaseGunItem extends Item implements IMeatItem, IAnimatable, ISyncable
{
    public AnimationFactory factory = new AnimationFactory(this);
    Map<GunSounds, SoundEvent> sounds = new EnumMap<GunSounds, SoundEvent>(GunSounds.class);
    public Item ammunition;
    public boolean hasLore;
    public final int maxShots;
    public final int cooldown;
    public static final int ANIM_FIRE = 0;
    public static final int ANIM_RELOAD = 1;
    protected final Random rand = new Random(0);
    protected String registryName;

    public BaseGunItem(String registryName, Item ammunition, int maxShots, int cooldown, boolean hasLore, FabricItemSettings settings)
    {
        super(settings.group(NMItemGroups.WEAPONS).maxCount(1).maxDamage(maxShots).maxDamageIfAbsent(maxShots));
        this.registryName = registryName;

        this.ammunition = ammunition;
        this.maxShots = maxShots;
        this.hasLore = hasLore;
        this.cooldown = cooldown;
        GeckoLibNetwork.registerSyncable(this);
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        if (hasLore)
        {
            tooltip.add(new TranslatableText("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.NONE;
    }

    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        fire(world, user, itemStack);
        return TypedActionResult.fail(itemStack);
    }

    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        if (cursorStackReference.get().getItem().equals(ammunition) && stack.getDamage() != 0)
        {
            this.reload(player, stack);
            cursorStackReference.get().decrement(1);
            return true;
        }
        return false;
    }

    public abstract void fire(World world, PlayerEntity user, ItemStack stack);

    public abstract Vec3f getAimOffset();

    // Should only be called on server.
    public void reload(PlayerEntity user, ItemStack stack)
    {
        user.getItemCooldownManager().set(this, 7);
        ItemStack ammo = getStack(this.ammunition, user);
        // If ammunition was found in inventory.
        if (ammo != null)
        {
            stack.setDamage(0);
            ammo.decrement(1);

            if (!user.world.isClient)
            {
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) user.world);
                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_RELOAD);
                // Do not sync reload animation with other players; it looks silly.

                // Play sound
                playSound(user.world, user, GunSounds.RELOAD);
            }
        }
    }

    // TODO: Currently reaches through blocks.
    public List<EntityHitResult> getRayTargets(PlayerEntity caster, Vec3d startPos, Vec3d endPos, Predicate<Entity> predicate, double margin)
    {
        World world = caster.world;


        Box box = caster.getBoundingBox().stretch(endPos.subtract(startPos)).expand(1.0, 1.0, 1.0);

        // Remove entities not intersecting with the ray
        List<EntityHitResult> list = new ArrayList<>();
        world.getOtherEntities(caster, box, predicate).forEach(entity ->
        {
            Optional<Vec3d> optional = entity.getBoundingBox().expand(entity.getTargetingMargin() + margin).raycast(startPos, endPos);
            optional.ifPresent(vec3d -> list.add(new EntityHitResult(entity, vec3d)));
        });

        return list;
    }

    public Optional<LivingEntity> hitScan(PlayerEntity caster, double distance, float tickDelta)
    {
        World world = caster.world;
        if (!world.isClient)
        {
            Vec3d pos = caster.getCameraPosVec(tickDelta);
            Vec3d look = caster.getRotationVec(tickDelta);
            Vec3d end = pos.add(look.multiply(distance));

            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(pos, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.collides() && entity instanceof LivingEntity;

            double minDistance = distance;
            Entity entity = null;
            EntityHitResult entityResult = null;
            for (EntityHitResult result : getRayTargets(caster, pos, blockResult.getPos(), entityFilter, 0.1))
            {
                if (result.getPos().distanceTo(pos) < minDistance)
                {
                    minDistance = result.getPos().distanceTo(pos);
                    entity = result.getEntity();
                    entityResult = result;
                }
            }

            Vec3d hitPos;
            hitPos = Objects.requireNonNullElse(entityResult, blockResult).getPos();
//            world.addParticle(ParticleTypes.ANGRY_VILLAGER, hitPos.x, hitPos.y, hitPos.z, 0, 0, 0);
            BeamRenderer.p0 = pos;
            BeamRenderer.p1 = hitPos;
            System.out.println(hitPos);

            return Optional.ofNullable((LivingEntity) entity);
        }
        return Optional.empty();
    }

    // Remove ammunition from inventory. Returns null if none present.
    public ItemStack getStack(Item type, PlayerEntity player)
    {
        for (int i = 0; i < player.getInventory().size(); ++i)
        {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem().equals(type))
            {
                return stack;
            }
        }
        return null;
    }

    public void playSound(World world, PlayerEntity player, GunSounds sound)
    {
        if (sounds.containsKey(sound))
        {
//            System.out.println(sound);
            world.playSound(
                    null,
                    player.getBlockPos(),
                    sounds.get(sound),
                    SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    public enum GunSounds
    {
        FIRE_PRIMARY,
        RELOAD,
        EMPTY,
    }
}
