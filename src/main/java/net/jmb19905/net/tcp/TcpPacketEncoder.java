package net.jmb19905.net.tcp;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.MessageToByteEncoder;
import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;
import net.jmb19905.util.crypto.Encryption;

public class TcpPacketEncoder extends MessageToByteEncoder<Packet> {

    private Encryption encryption = null;

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    @Override
    protected Buffer allocateBuffer(ChannelHandlerContext ctx, Packet msg) {
        return ctx.bufferAllocator().allocate(256);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) {
        BufferWrapper buffer = new BufferWrapper(out);
        Packet.deconstructPacket(msg, buffer);
        if (encryption == null) return;
        buffer.encrypt(encryption);
    }
}
