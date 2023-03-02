package net.jmb19905.net.event;

import net.jmb19905.net.packet.Packet;
import org.jetbrains.annotations.NotNull;

public class ReadEvent extends NetworkEvent {
    public static final String ID = "read";
    public final Packet packet;
    public ReadEvent(@NotNull NetworkEventContext ctx, Packet packet) {
        super(ctx, ID);
        this.packet = packet;
    }
}
