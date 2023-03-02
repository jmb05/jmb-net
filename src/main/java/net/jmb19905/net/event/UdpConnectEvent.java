package net.jmb19905.net.event;

import org.jetbrains.annotations.NotNull;

public class UdpConnectEvent extends NetworkEvent{
    public static final String ID = "udp_connect";
    public UdpConnectEvent(@NotNull NetworkEventContext ctx) {
        super(ctx, ID);
    }
}
