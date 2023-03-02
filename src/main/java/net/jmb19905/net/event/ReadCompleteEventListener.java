package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface ReadCompleteEventListener extends EventListener<ReadCompleteEvent> {
    @Override
    default String getId() {
        return ReadCompleteEvent.ID;
    }
}
