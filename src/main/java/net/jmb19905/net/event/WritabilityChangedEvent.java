package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class WritabilityChangedEvent extends NetworkEvent {
    public static final String ID = "writability_changed";
    public WritabilityChangedEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
