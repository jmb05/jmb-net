package net.jmb19905.net;

import io.netty5.channel.Channel;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.crypto.Encryption;

import java.net.SocketAddress;

public interface NetThread extends Runnable {

    void start();
    void stop();
    Channel getChannel(SocketAddress addr);
    PacketChannelHandler getHandler(SocketAddress addr);
    void setEncryption(SocketAddress addr, Encryption encryption);
    Encryption getEncryption(SocketAddress addr);

}
