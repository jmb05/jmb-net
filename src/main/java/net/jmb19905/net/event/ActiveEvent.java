package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class ActiveEvent extends NetworkEvent{

    public static final String ID = "active";

    public ActiveEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
