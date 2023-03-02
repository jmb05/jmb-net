package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class ReadCompleteEvent extends NetworkEvent{
    public static final String ID = "read_complete";
    public ReadCompleteEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
