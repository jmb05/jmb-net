package net.jmb19905.net.tcp;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;
import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;

import java.util.Optional;

public class TcpPacketDecoder extends ByteToMessageDecoder {

    private Encryption encryption = null;

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        BufferWrapper buffer = new BufferWrapper(in);
        Logger.trace("Received buffer - Size: " + buffer.getSize());
        if (encryption != null) buffer.decrypt(encryption, 1, in.readableBytes() - 1);
        buffer.getEncryptionByte();
        Optional<Packet> packetOpt = Packet.constructPacket(buffer);
        if (packetOpt.isEmpty()) return;
        Packet packet = packetOpt.get();
        Logger.trace("Decoded Tcp Packet: " + packet);
        ctx.fireChannelRead(packet);
        buffer.clear();
    }
}
