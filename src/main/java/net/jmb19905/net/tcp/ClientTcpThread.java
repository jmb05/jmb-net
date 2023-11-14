package net.jmb19905.net.tcp;

import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.*;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioSocketChannel;
import net.jmb19905.net.NetThread;
import net.jmb19905.net.event.NetworkEvent;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;
import net.jmb19905.util.events.EventListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientTcpThread implements NetThread, Runnable {
    
    private final Thread thread = new Thread(this);
    private final EventLoopGroup group;
    private final InetSocketAddress serverAddress;
    private Channel channel;
    private final PacketChannelHandler handler;
    private final TcpPacketEncoder encoder;
    private final TcpPacketDecoder decoder;
    private Encryption encryption;

    public ClientTcpThread(String server, int port) {
        InetSocketAddress serverAddress1;
        try {
            serverAddress1 = InetSocketAddress.createUnresolved(server, port);
        } catch (IllegalArgumentException e) {
            Logger.error(e);
            serverAddress1 = null;
        }
        this.serverAddress = serverAddress1;
        group = new MultithreadEventLoopGroup(NioHandler.newFactory());
        handler = new PacketChannelHandler("client_tcp");
        encoder = new TcpPacketEncoder();
        decoder = new TcpPacketDecoder();
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        try {
            channel.close();
            thread.join();
        } catch (InterruptedException e) {
            Logger.error(e);
        }
    }

    public InetSocketAddress getServerAddress() {
        return this.serverAddress;
    }

    /** @deprecated */
    @Deprecated
    public Channel getChannel(SocketAddress addr) {
        return this.channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    /** @deprecated */
    @Deprecated
    public PacketChannelHandler getHandler(SocketAddress addr) {
        return this.handler;
    }

    public PacketChannelHandler getHandler() {
        return this.handler;
    }

    /** @deprecated */
    @Deprecated
    public void setEncryption(SocketAddress addr, Encryption encryption) {
        this.encryption = encryption;
        this.encoder.setEncryption(encryption);
        this.decoder.setEncryption(encryption);
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
        this.encoder.setEncryption(encryption);
        this.decoder.setEncryption(encryption);
    }

    /** @deprecated */
    @Deprecated
    public Encryption getEncryption(SocketAddress addr) {
        return this.encryption;
    }

    public Encryption getEncryption() {
        return this.encryption;
    }

    public <L extends EventListener<? extends NetworkEvent>> void addEventListener(L listener) {
        this.handler.addEventListener(listener);
    }

    @Override
    public void run() {
        if (serverAddress == null) {
            Logger.error("Cannot start client Server Address not valid");
            return;
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("encoder", encoder);
                            ch.pipeline().addLast("decoder", decoder);
                            ch.pipeline().addLast("handler", handler);
                        }
                    });

            channel = bootstrap.connect(serverAddress).asStage().get();
            channel.closeFuture().asStage().sync();
        } catch (Exception e) {
            handler.channelExceptionCaught(null, e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
