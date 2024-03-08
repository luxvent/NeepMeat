package com.neep.meatlib.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface EntityLookEvents
{
    Event<EntityLookEvents> CHANGE_LOOK = EventFactory.createArrayBacked(EntityLookEvents.class,
            (listeners) -> ((pitch, yaw) ->
            {
                for (var listener : listeners)
                {
                    listener.onChangeLookDirection(pitch, yaw);
                }
            }));

    void onChangeLookDirection(float pitch, float yaw);
}
