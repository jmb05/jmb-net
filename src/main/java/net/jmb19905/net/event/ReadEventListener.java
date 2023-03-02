package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface ReadEventListener extends EventListener<ReadEvent> {
    @Override
    default String getId() {
        return ReadEvent.ID;
    }
}
