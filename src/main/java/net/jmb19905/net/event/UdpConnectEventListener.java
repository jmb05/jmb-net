package net.jmb19905.net.event;

import net.jmb19905.util.events.EventListener;

public interface UdpConnectEventListener extends EventListener<UdpConnectEvent> {
    @Override
    default String getId() {
        return UdpConnectEvent.ID;
    }
}
