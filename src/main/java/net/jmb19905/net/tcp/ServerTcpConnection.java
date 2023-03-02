package net.jmb19905.net.tcp;

import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.*;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import net.jmb19905.net.Connection;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;

import java.util.concurrent.ExecutionException;

public class ServerTcpConnection implements Connection, Runnable {

    private final Thread thread;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final int port;
    private Channel channel;
    private final PacketChannelHandler handler;
    private final TcpPacketEncoder encoder;
    private final TcpPacketDecoder decoder;
    private Encryption encryption;

    public ServerTcpConnection(int port) {
        this.port = port;
        thread = new Thread(this);
        bossGroup = new MultithreadEventLoopGroup(NioHandler.newFactory());
        workerGroup = new MultithreadEventLoopGroup(NioHandler.newFactory());
        handler = new PacketChannelHandler("server_tcp");
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
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast("encoder", encoder);
                            ch.pipeline().addLast("decoder", decoder);
                            ch.pipeline().addLast("handler", handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128);
            channel = serverBootstrap.bind(port).asStage().get();
            channel.closeFuture().asStage().sync();
        } catch (ExecutionException | InterruptedException e) {
            Logger.error(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
