package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class RegisterEvent extends NetworkEvent {
    public static final String ID = "register";
    public RegisterEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
