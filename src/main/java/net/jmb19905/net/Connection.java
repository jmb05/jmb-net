package net.jmb19905.net;

import io.netty5.channel.Channel;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.crypto.Encryption;

public interface Connection extends Runnable {

    void start();
    void stop();
    Channel getChannel();
    PacketChannelHandler getHandler();
    void setEncryption(Encryption encryption);
    Encryption getEncryption();

}
