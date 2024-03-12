package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    private boolean dirty = true;
    private boolean invalidated = false;

    private final RecoilManager recoil = new RecoilManager();

    private final BaseModule root = new BaseModule();

    public MeatgunComponentImpl(ItemStack stack, ComponentKey<MeatgunComponent> key)
    {
        super(stack, key);
        root.readNbt(getOrCreateRootTag());
    }

    @Override
    public MeatgunModule getRoot()
    {
        return root;
    }

    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.trigger(world, player, stack, id, pitch, yaw, handType);
//        var module = root.getChildren().get(0).get();
//        if (module instanceof BosherModule)
//        {
//            root.getChildren().get(0).set(new UnderbarrelModule());
//            markDirty();
//        }
//        else
//        {
//            root.getChildren().get(0).set(new BosherModule());
//            markDirty();
//        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.tickTrigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public RecoilManager getRecoil()
    {
        return recoil;
    }

    @Override
    public void tick()
    {
        root.tick();

        if (dirty)
        {
            root.writeNbt(getOrCreateRootTag());
            dirty = false;
        }

        if (invalidated)
        {
            root.readNbt(getOrCreateRootTag());
            invalidated = false;
        }
    }

    @Override
    public void markDirty()
    {
        dirty = true;
    }

    @Override
    public void onTagInvalidated()
    {
        super.onTagInvalidated();
        invalidated = true;
    }
}
