package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface ActiveEventListener extends EventListener<ActiveEvent> {
    @Override
    default String getId() {
        return ActiveEvent.ID;
    }
}
