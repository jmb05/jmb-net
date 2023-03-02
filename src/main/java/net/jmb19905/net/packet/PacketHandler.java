package net.jmb19905.net.packet;

public interface PacketHandler<P extends Packet> {
    void handle(P packet);
}
