package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.MeatFluidHelper;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class MeatAttribtuteHandler implements FluidVariantAttributeHandler
{
    // TODO: Move this out of client package
    public Text getName(FluidVariant fluidVariant)
    {
        float hunger = MeatFluidHelper.getHunger(fluidVariant);
        float saturation = MeatFluidHelper.getSaturation(fluidVariant);
        return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName()
                .append(Text.of(" (H: " + String.format("%.2f", hunger) + ", S: " + String.format("%.2f",saturation) + ")"));
    }
}
