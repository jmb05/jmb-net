package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface ExceptionEventListener extends EventListener<ExceptionEvent> {
    @Override
    default String getId() {
        return ExceptionEvent.ID;
    }
}
