package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.List;

public class CrusherProcess implements Process
{
    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.ITEM_INPUT, LivingMachineComponents.CRUSHER_SEGMENT, LivingMachineComponents.ITEM_OUTPUT, LivingMachineComponents.MOTOR_PORT).ifPresent(result ->
        {
            var hoppers = result.t1();
            var crushers = result.t2();
            var itemOutputs = result.t3();

            if (!be.getComponent(LivingMachineComponents.LUCKY_ONE).isEmpty())
            {
//                NeepMeat.LOGGER.info("Yay!");
            }

            if (be.getPower() < 0.1)
            {
                return;
            }

            float progressIncrement = be.getProgressIncrement() / crushers.size() * 4;

            Storage<ItemVariant> input = hoppers.iterator().next().getStorage(null);
            Storage<ItemVariant> output = itemOutputs.iterator().next().getStorage(null);

            boolean hasInput = input.nonEmptyIterator().hasNext();
            try (Transaction transaction = Transaction.openOuter())
            {
                for (var crusher : crushers)
                {
                    try (Transaction inner = transaction.openNested())
                    {
                        CrusherSegmentBlockEntity.InputSlot slot = crusher.getStorage();

                        if (hasInput && slot.isEmpty())
                            StorageUtil.move(input, slot, v -> true, 1, inner);

                        if (!slot.isEmpty())
                        {
                            slot.tick(progressIncrement, output, inner);
                        }
                        inner.commit();
                    }
                }
                transaction.commit();
            }
        });
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return List.of(
                LivingMachineComponents.ITEM_INPUT,
                LivingMachineComponents.CRUSHER_SEGMENT,
                LivingMachineComponents.ITEM_OUTPUT,
                LivingMachineComponents.MOTOR_PORT
        );
    }
}
