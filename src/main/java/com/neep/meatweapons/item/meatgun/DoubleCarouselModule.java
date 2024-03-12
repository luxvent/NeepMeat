package com.neep.meatweapons.item.meatgun;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.List;

public class DoubleCarouselModule extends AbstractMeatgunModule
{
//    static Codec<DoubleCarouselModule> CODEC = Codec.of(
//            new Encoder<>()
//            {
//                @Override
//                public <T> DataResult<T> encode(DoubleCarouselModule input, DynamicOps<T> ops, T prefix)
//                {
//                    return null;
//                }
//            },
//            new Decoder<>()
//            {
//                @Override
//                public <T> DataResult<Pair<DoubleCarouselModule, T>> decode(DynamicOps<T> ops, T input)
//                {
//                    var list = ops.getList(input);
//                    return Pair.of();
//                }
//            }
//    );

//    static Codec<DoubleCarouselModule> CODEC = RecordCodecBuilder.create(instance ->
//    {
//        instance.group(
//        )
//    })

    private final ModuleSlot upSlot = new SimpleModuleSlot(new Matrix4f().translate(0, 4 / 16f, -3 / 16f));
    private final ModuleSlot downSlot = new SimpleModuleSlot(new Matrix4f().rotateZ(MathHelper.PI).translate(0, 4 / 16f, -3 / 16f));
    private final ModuleSlot auxSlot = new SimpleModuleSlot(new Matrix4f().rotateY(MathHelper.PI).translate(0, 5 / 16f, 0 / 16f));

    public DoubleCarouselModule()
    {
        setSlots(List.of(upSlot, downSlot, auxSlot));

        upSlot.set(new LongBoiModule());
        downSlot.set(new BosherModule());
        auxSlot.set(new BatteryModule());
    }

    public DoubleCarouselModule(NbtCompound nbt)
    {
        this();
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.DOUBLE_CAROUSEL;
    }

}
