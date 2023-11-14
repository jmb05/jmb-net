package net.jmb19905.net.handler;

import io.netty5.channel.ChannelHandlerAdapter;
import io.netty5.channel.ChannelHandlerContext;
import net.jmb19905.net.event.*;
import net.jmb19905.net.packet.Packet;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.util.Logger;
import net.jmb19905.util.events.EventHandler;
import net.jmb19905.util.events.EventListener;

import java.util.Optional;

public class PacketChannelHandler extends ChannelHandlerAdapter {

    private final EventHandler<NetworkEventContext> eventHandler;

    public PacketChannelHandler(String id) {
        this.eventHandler = new EventHandler<>(id);
        this.eventHandler.setValid(true);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        eventHandler.performEvent(new ActiveEvent(createContext(ctx)));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        eventHandler.performEvent(new InactiveEvent(createContext(ctx)));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        eventHandler.performEvent(new RegisterEvent(createContext(ctx)));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        Optional<PacketHandler<Packet>> handlerOpt = packet.getHandler();
        if (handlerOpt.isEmpty()) {
            Logger.error("No Packet Handler registered for Packet: " + packet + " on this side");
            return;
        }
        handlerOpt.get().handle(new HandlingContext(ctx, this), packet);
        eventHandler.performEvent(new ReadEvent(createContext(ctx), packet));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        eventHandler.performEvent(new ReadCompleteEvent(createContext(ctx)));
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        eventHandler.performEvent(new WritabilityChangedEvent(createContext(ctx)));
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logger.error(cause);
        eventHandler.performEvent(new ExceptionEvent(createContext(ctx), cause));
    }

    private NetworkEventContext createContext(ChannelHandlerContext ctx) {
        return new NetworkEventContext(this, ctx);
    }

    public <L extends EventListener<? extends NetworkEvent>> void addEventListener(L listener) {
        eventHandler.addEventListener(listener);
    }
}