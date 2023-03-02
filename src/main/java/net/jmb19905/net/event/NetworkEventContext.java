package net.jmb19905.net.event;

import io.netty5.channel.ChannelHandlerContext;
import net.jmb19905.net.handler.PacketChannelHandler;
import net.jmb19905.util.events.EventContext;

public class NetworkEventContext extends EventContext<PacketChannelHandler> {

    private final PacketChannelHandler handler;
    private final ChannelHandlerContext ctx;

    public NetworkEventContext(PacketChannelHandler handler, ChannelHandlerContext ctx) {
        super(handler);
        this.handler = handler;
        this.ctx = ctx;
    }

    public ContextFuture<NetworkEventContext> send(Object o) {
        ContextFuture<NetworkEventContext> contextFuture = new ContextFuture<>(this);
        this.ctx.writeAndFlush(o).addListener(l -> contextFuture.perform());
        return contextFuture;
    }

    public PacketChannelHandler getHandler() {
        return handler;
    }

    public ChannelHandlerContext getContext() {
        return ctx;
    }
}
