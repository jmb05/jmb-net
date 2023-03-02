package net.jmb19905.net.packet;

import net.jmb19905.net.buffer.BufferSerializable;
import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.util.crypto.Encryption;

import java.net.InetSocketAddress;
import java.util.Optional;

public abstract class Packet implements BufferSerializable {

    private InetSocketAddress address;

    public abstract String getId();

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public static void deconstructPacket(Packet packet, BufferWrapper buffer) {
        buffer.putString(packet.getId());
        buffer.put(packet);
    }

    @SuppressWarnings("unchecked")
    public static Optional<Packet> constructPacket(BufferWrapper buffer) {
        String header = buffer.getString();
        Optional<PacketType<? extends Packet>> packetTypeOpt = PacketRegistry.getInstance().getPacketType(header);
        if (packetTypeOpt.isEmpty()) return Optional.empty();
        return (Optional<Packet>) buffer.get(packetTypeOpt.get().getPacketClass());
    }

    @SuppressWarnings("unchecked")
    public <P extends Packet> Optional<PacketHandler<P>> getHandler() {
        Optional<PacketType<? extends Packet>> packetTypeOpt = PacketRegistry.getInstance().getPacketType(getId());
        if (packetTypeOpt.isEmpty()) return Optional.empty();
        PacketType<P> packetType = (PacketType<P>) packetTypeOpt.get();
        return Optional.of(packetType.getHandler());
    }

}