package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface InactiveEventListener extends EventListener<InactiveEvent> {
    @Override
    default String getId() {
        return InactiveEvent.ID;
    }
}
