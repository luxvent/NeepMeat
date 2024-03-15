package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    private final RecoilManager recoil = new RecoilManager();
    private final BaseModule root = new BaseModule(this::markDirty);
    private boolean dirty = true;
    private boolean invalidated = false;

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
        if (root != null)
            root.writeNbt(getOrCreateRootTag());
    }

    @Override
    @Nullable
    public MeatgunModule find(UUID uuid)
    {
        return findRecursive(root, uuid);
    }

    @Nullable
    private MeatgunModule findRecursive(MeatgunModule module, UUID uuid)
    {
        if (module.getUuid().equals(uuid))
            return module;

        for (var slot : module.getChildren())
        {
            MeatgunModule child = slot.get();
            if (child == MeatgunModule.DEFAULT)
                continue;

            if (child.getUuid().equals(uuid))
                return child;

            return findRecursive(child, uuid);
        }
        return null;
    }

    @Override
    public void onTagInvalidated()
    {
        super.onTagInvalidated();
        dirty = true;
        invalidated = true;
    }
}
