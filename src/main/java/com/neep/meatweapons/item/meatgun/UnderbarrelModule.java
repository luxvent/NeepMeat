package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class UnderbarrelModule extends AbstractMeatgunModule
{
    private final ModuleSlot upSlot = new SimpleModuleSlot(new Matrix4f().translate(0, 0, -3 / 16f));
    private final ModuleSlot downSlot = new SimpleModuleSlot(new Matrix4f().rotateZ(MathHelper.PI).translate(0, 3 / 16f, -3 / 16f));
    private final List<ModuleSlot> slots = new ArrayList<>();

    public UnderbarrelModule()
    {
        upSlot.set(new LongBoiModule());
        downSlot.set(new BosherModule());
        slots.add(upSlot);
        slots.add(downSlot);
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 1)
            upSlot.get().trigger(world, player, stack, 0, pitch, yaw, handType);

        if (id == 2)
            downSlot.get().trigger(world, player, stack, 0, pitch, yaw, handType);
    }

    @Override
    public List<ModuleSlot> getChildren()
    {
        return slots;
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.UNDERBARREL;
    }
}
