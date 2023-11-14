package net.jmb19905.net;

import io.netty5.util.concurrent.Future;
import net.jmb19905.net.event.ContextFuture;
import net.jmb19905.net.packet.Packet;

import java.net.SocketAddress;

public class NetworkingUtility {

    private static <N extends NetThread> Future<Void> __internal_send(N netThread, SocketAddress address, Packet packet) {
        return netThread.getChannel(address).writeAndFlush(packet);
    }

    public static <N extends NetThread> ContextFuture<N> send(N netThread, SocketAddress address, Packet packet) {
        ContextFuture<N> ctxFuture = new ContextFuture<>(netThread);
        __internal_send(netThread, address, packet).addListener((l) -> ctxFuture.perform());
        return ctxFuture;
    }

}
