package net.jmb19905.net.handler;

import io.netty5.channel.ChannelHandlerContext;
import net.jmb19905.net.event.ContextFuture;
import net.jmb19905.net.packet.Packet;

public record HandlingContext(ChannelHandlerContext ctx, PacketChannelHandler handler) {

    public ContextFuture<HandlingContext> send(Packet packet) {
        ContextFuture<HandlingContext> contextFuture = new ContextFuture<>(this);
        ctx.writeAndFlush(packet).addListener(l -> contextFuture.perform());
        return contextFuture;
    }

}
