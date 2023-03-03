package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface WritabilityChangedEventListener extends EventListener<WritabilityChangedEvent> {
    @Override
    default String getId() {
        return WritabilityChangedEvent.ID;
    }
}
