package net.jmb19905.net.event;

import net.jmb19905.util.events.Event;
import org.jetbrains.annotations.NotNull;

public abstract class NetworkEvent extends Event<NetworkEventContext> {
    public NetworkEvent(@NotNull NetworkEventContext ctx, String id) {
        super(ctx, id);
    }
}
