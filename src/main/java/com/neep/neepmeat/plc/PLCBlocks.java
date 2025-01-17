package com.neep.neepmeat.plc;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.surgical_controller.PLCBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import com.neep.neepmeat.plc.block.ExecutorBlock;
import com.neep.neepmeat.plc.block.PLCRedstoneInterface;
import com.neep.neepmeat.plc.block.PLCRedstoneInterfaceBlockEntity;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import com.neep.neepmeat.plc.block.entity.ExecutorBlockEntity;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.instruction.gui.InstructionAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class PLCBlocks
{
    public static BlockEntityType<PLCRedstoneInterfaceBlockEntity> REDSTONE_INTERFACE_ENTITY;
    public static BlockEntityType<RoboticArmBlockEntity> ROBOTIC_ARM_ENTITY;
    public static BlockEntityType<ExecutorBlockEntity> EXECUTOR_ENTITY;

    public static final Block ROBOTIC_ARM = BlockRegistry.queue(new RoboticArmBlock("robotic_arm", ItemSettings.block().tooltip(TooltipSupplier.hidden(1)).requiresMotor().plcActuator(), MeatlibBlockSettings.create().hardness(3)));;
    public static final Block REDSTONE_INTERFACE = BlockRegistry.queue(new PLCRedstoneInterface("redstone_interface", MeatlibBlockSettings.create().hardness(2)));
    public static final Block EXECUTOR = BlockRegistry.queue(new ExecutorBlock("executor", ItemSettings.block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.create().nonOpaque().hardness(2)));
    public static final Block PLC = BlockRegistry.queue(new PLCBlock("plc", NMBlocks.block(), MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS)));
    public static BlockEntityType<PLCBlockEntity> PLC_ENTITY;

    public static void init()
    {
        ROBOTIC_ARM_ENTITY = NMBlockEntities.register("robotic_arm", (pos, state) -> new RoboticArmBlockEntity(ROBOTIC_ARM_ENTITY, pos, state), ROBOTIC_ARM);
        REDSTONE_INTERFACE_ENTITY = NMBlockEntities.register("redstone_interface", (pos, state) -> new PLCRedstoneInterfaceBlockEntity(REDSTONE_INTERFACE_ENTITY, pos, state), REDSTONE_INTERFACE);
        RedstoneInterface.LOOKUP.registerSelf(REDSTONE_INTERFACE_ENTITY);

        PLC_ENTITY = NMBlockEntities.register("plc", (pos, state) -> new PLCBlockEntity(PLCBlocks.PLC_ENTITY, pos, state), PLC);
        EXECUTOR_ENTITY = NMBlockEntities.register("executor", (pos, state) -> new ExecutorBlockEntity(PLCBlocks.EXECUTOR_ENTITY, pos, state), EXECUTOR);

        InstructionAttributes.init();
    }
}
