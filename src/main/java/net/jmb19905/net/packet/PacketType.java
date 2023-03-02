package net.jmb19905.net.packet;

import net.jmb19905.util.Logger;
import net.jmb19905.util.registry.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class PacketType<P extends Packet> extends Type<P> {

    private final Class<P> packetClass;
    private final PacketHandler<P> handler;

    public PacketType(Class<P> packetClass, PacketHandler<P> handler) {
        this.packetClass = packetClass;
        this.handler = handler;
    }

    public Class<P> getPacketClass() {
        return packetClass;
    }

    public PacketHandler<P> getHandler() {
        return handler;
    }

    public Optional<P> createPacket() {
        if (packetClass == null) return Optional.empty();
        try {
            Constructor<P> constructor = packetClass.getConstructor();
            return Optional.of(constructor.newInstance());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Logger.error(e);
            return Optional.empty();
        }
    }

}
