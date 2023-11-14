package net.jmb19905.net.tcp;

import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.*;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import net.jmb19905.net.NetThread;
import net.jmb19905.net.event.ActiveEventListener;
import net.jmb19905.net.event.InactiveEventListener;
import net.jmb19905.net.event.NetworkEvent;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;
import net.jmb19905.util.events.EventListener;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ServerTcpThread implements NetThread {

    private final Thread thread;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final int port;
    private Channel channel;
    private final Map<SocketAddress, Remote> connectedClients = new HashMap<>();
    private final List<EventListener<? extends NetworkEvent>> defaultEvents = new ArrayList<>();

    public ServerTcpThread(int port) {
        this.port = port;
        thread = new Thread(this);
        bossGroup = new MultithreadEventLoopGroup(NioHandler.newFactory());
        workerGroup = new MultithreadEventLoopGroup(NioHandler.newFactory());
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
    public Channel getChannel(SocketAddress address) {
        return this.connectedClients.get(address).channel();
    }

    @Override
    public PacketChannelHandler getHandler(SocketAddress address) {
        return this.connectedClients.get(address).handler();
    }

    public void setEncryption(SocketAddress address, Encryption encryption) {
        this.connectedClients.get(address).setEncryption(encryption);
        this.connectedClients.get(address).encoder.setEncryption(encryption);
        this.connectedClients.get(address).decoder.setEncryption(encryption);
    }

    public Encryption getEncryption(SocketAddress address) {
        return this.connectedClients.get(address).encryption();
    }

    public Map<SocketAddress, Remote> getConnectedClients() {
        return connectedClients;
    }

    public <L extends EventListener<? extends NetworkEvent>> void addDefaultEventListener(L listener) {
        this.connectedClients.values().stream()
                .map(Remote::handler)
                .forEach(handler -> handler.addEventListener(listener));
        this.defaultEvents.add(listener);
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
                            TcpPacketEncoder encoder = new TcpPacketEncoder();
                            TcpPacketDecoder decoder = new TcpPacketDecoder();
                            PacketChannelHandler handler = new PacketChannelHandler("server_tcp");
                            ch.pipeline().addLast("encoder", encoder);
                            ch.pipeline().addLast("decoder", decoder);
                            ch.pipeline().addLast("handler", handler);
                            Remote remote = new Remote(ch, handler, encoder, decoder, new Encryption());
                            handler.addEventListener((InactiveEventListener) evt -> ServerTcpThread.this.connectedClients.remove(evt.getContext().remoteAddress()));
                            handler.addEventListener((ActiveEventListener) evt -> ServerTcpThread.this.connectedClients.put(evt.getContext().remoteAddress(), remote));
                            ServerTcpThread.this.defaultEvents.forEach(handler::addEventListener);
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

    private static final class Remote {
        private final Channel channel;
        private final PacketChannelHandler handler;
        private final TcpPacketEncoder encoder;
        private final TcpPacketDecoder decoder;
        private Encryption encryption;

        public Remote(Channel channel, PacketChannelHandler handler, TcpPacketEncoder encoder, TcpPacketDecoder decoder, Encryption encryption) {
            this.channel = channel;
            this.handler = handler;
            this.encoder = encoder;
            this.decoder = decoder;
            this.encryption = encryption;
        }

        public Channel channel() {
            return channel;
        }

        public PacketChannelHandler handler() {
            return handler;
        }

        public TcpPacketEncoder encoder() {
            return encoder;
        }

        public TcpPacketDecoder decoder() {
            return decoder;
        }

        public Encryption encryption() {
            return encryption;
        }

        public void setEncryption(Encryption encryption) {
            this.encryption = encryption;
        }
    }

}
