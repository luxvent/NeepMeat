package com.neep.meatweapons.client.renderer;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.entity.BounceGrenadeEntity;
import net.minecraft.util.math.Direction;

public class BounceGrenadeInstance extends EntityInstance<BounceGrenadeEntity> implements DynamicInstance
{
    private final ModelData model;

    public BounceGrenadeInstance(MaterialManager materialManager, BounceGrenadeEntity entity)
    {
        super(materialManager, entity);
        this.model = materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(MWExtraModels.BOUNCE_GRENADE).createInstance();
        model.translate(getInstancePosition());
    }

    @Override
    protected void remove()
    {
        model.delete();
    }

    @Override
    public void beginFrame()
    {
        float tickDelta = AnimationTickHolder.getPartialTicks();
        float renderTime = AnimationTickHolder.getRenderTime();
        model.loadIdentity()
                .translate(getInstancePosition(tickDelta))
                .rotate(Direction.UP, renderTime / 4)
                .rotate(Direction.NORTH, renderTime / 5)
        ;
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), model);
    }
}
