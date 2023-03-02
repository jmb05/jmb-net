package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface RegisterEventListener extends EventListener<RegisterEvent> {
    @Override
    default String getId() {
        return RegisterEvent.ID;
    }
}
