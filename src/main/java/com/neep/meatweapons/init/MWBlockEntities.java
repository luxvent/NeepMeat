package com.neep.meatweapons.init;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.block.entity.TinkerTableBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MWBlockEntities
{
    public static BlockEntityType<TinkerTableBlockEntity> TINKER_TABLE;

    public static void init()
    {
        TINKER_TABLE = register("tinker_table", (pos, state) -> new TinkerTableBlockEntity(TINKER_TABLE, pos, state), MWBlocks.TINKER_TABLE);
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... block)
    {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(MeatWeapons.NAMESPACE, id),
                FabricBlockEntityTypeBuilder.create(factory, block).build());
    }
}
