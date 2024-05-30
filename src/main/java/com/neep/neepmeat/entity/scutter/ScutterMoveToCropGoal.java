package com.neep.neepmeat.entity.scutter;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class ScutterMoveToCropGoal extends MoveToTargetPosGoal
{
    private final FarmingScutter scutter;

    public ScutterMoveToCropGoal(FarmingScutter scutter, double speed, int range)
    {
        super(scutter, speed, range);
        this.scutter = scutter;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        if (cooldown > 0 && scutter.getTargets().isEmpty())
        {
            cooldown--;
        }
        else if (!scutter.getTargets().isEmpty())
        {
            cooldown = getInterval(mob);
            targetPos = scutter.getTargets().iterator().next();
            return true;
        }
        return false;
    }

    @Override
    protected int getInterval(PathAwareEntity mob)
    {
        return 10;
    }

    @Override
    public boolean shouldContinue()
    {
        return super.shouldContinue() && !hasReached() && !scutter.getTargets().isEmpty();
    }

    @Override
    public void tick()
    {
        if (scutter.getTargets().isEmpty())
            return;

        if (hasReached())
        {
            scutter.getTargets().remove(targetPos);
        }

        super.tick();
    }

    @Override
    public double getDesiredDistanceToTarget()
    {
        return 0.5f;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos)
    {
        return pos.equals(targetPos);
    }
}
