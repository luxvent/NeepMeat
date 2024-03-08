package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunS2C;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

import static com.neep.meatweapons.item.BaseGunItem.hitScan;

public class ChuggerModule implements MeatgunModule
{
    private final int maxShots = 16;
    private int shotsRemaining = maxShots;
    private final Random shotRandom = Random.create();

    public ChuggerModule()
    {

    }

    @Override
    public List<MeatgunModule> getChildren()
    {
        return List.of();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.CHUGGER;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        {
            if (shotsRemaining >= 0)
            {
//                player.getItemCooldownManager().set(this, 2);

                if (!world.isClient)
                {
                    fireBeam(world, player, stack, pitch, yaw);
//                    if (!player.isCreative())
//                        stack.setDamage(stack.getDamage() + 1);
                }
            }
            else // Weapon is out of ammunition.
            {
                if (world.isClient)
                {
                    // Play empty sound.
                }
                else
                {
                    // Try to reload
//                    this.reload(player, stack, null);
                }
            }
        }
    }

    public Vec3d getMuzzleOffset(LivingEntity entity, ItemStack stack)
    {
        boolean sneak = entity.isSneaking();
        return new Vec3d(
                sneak ? 0 : entity.getMainHandStack().equals(stack) ? -0.2 : 0.2,
                sneak ? -0.25 : 0.1,
                .2);
    }

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack, double pitchd, double yawd)
    {
        double d = 0.5;
        double yaw = yawd + d * (shotRandom.nextFloat() - 0.5);
        double pitch = pitchd + d * (shotRandom.nextFloat() - 0.5);

        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitch).rotateY((float) -yaw);
        pos = pos.add(transform);

        Vec3d end = pos.add(player.getRotationVec(1)
                .multiply(40));
        Optional<Entity> target = hitScan(player, pos, end, 40, this::syncBeamEffect);
        if (target.isPresent())
        {
            Entity entity = target.get();
            target.get().damage(BulletDamageSource.create(world, player, 0.1f), 2);
            entity.timeUntilRegen = 0;
        }

//        syncAnimation(world, player, stack, "fire", true);
        MeatgunS2C.sendRecoil((ServerPlayerEntity) player, MeatgunS2C.RecoilDirection.UP, 7, 0.2f,0.7f, 0.03f);
        world.playSoundFromEntity(null, player, NMSounds.CHUGGER_FIRE, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BULLET_TRAIL, world, pos, end, velocity, 0.1f, 1);
        }
    }
}
