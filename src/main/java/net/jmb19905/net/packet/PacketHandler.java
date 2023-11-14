package net.jmb19905.net.packet;

import net.jmb19905.net.handler.HandlingContext;

public interface PacketHandler<P extends Packet> {
    void handle(HandlingContext ctx, P packet);
}
