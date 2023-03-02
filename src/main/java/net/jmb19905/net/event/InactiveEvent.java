package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class InactiveEvent extends NetworkEvent{
    public static final String ID = "inactive";
    public InactiveEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
