package net.jmb19905.net.tcp;

import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.*;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioSocketChannel;
import net.jmb19905.net.Connection;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class ClientTcpConnection implements Connection, Runnable {
    
    private final Thread thread;
    private final EventLoopGroup group;
    private final InetSocketAddress serverAddress;
    private Channel channel;
    private final PacketChannelHandler handler;
    private final TcpPacketEncoder encoder;
    private final TcpPacketDecoder decoder;
    private Encryption encryption;

    public ClientTcpConnection(String server, int port) {
        InetSocketAddress serverAddress1;
        this.thread = new Thread(this);
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

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public PacketChannelHandler getHandler() {
        return handler;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
        encoder.setEncryption(encryption);
        decoder.setEncryption(encryption);
    }

    public Encryption getEncryption() {
        return encryption;
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
        } catch (ExecutionException | InterruptedException e) {
            Logger.error(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
