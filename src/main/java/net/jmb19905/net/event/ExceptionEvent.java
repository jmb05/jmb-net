package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class ExceptionEvent extends NetworkEvent{

    public static final String ID = "exception";
    private final Throwable cause;

    public ExceptionEvent(@NotNull NetworkEventContext ctx, Throwable cause) {
        super(ctx, ID);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
