package net.jmb19905.net.packet;

import net.jmb19905.util.Logger;
import net.jmb19905.util.registry.Registry;

import java.util.Optional;

public class PacketRegistry extends Registry {

    private static final PacketRegistry instance = new PacketRegistry();

    public <P extends Packet> void register(Class<P> packetClass, PacketHandler<P> handler) {
        PacketType<P> type = new PacketType<>(packetClass, handler);
        Optional<P> packetOpt = type.createPacket();
        packetOpt.ifPresent(p -> super.register(p.getId(), type));
    }

    public Optional<PacketType<? extends Packet>> getPacketType(String id) {
        try {
            return Optional.of((PacketType<? extends Packet>) super.getRegistry(id));
        } catch (NullPointerException e) {
            Logger.error("No such PacketType registered: \"" + id + "\"");
            return Optional.empty();
        }
    }

    public static PacketRegistry getInstance() {
        return instance;
    }
}
