package net.jmb19905.net.test;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.net.packet.PacketRegistry;
import net.jmb19905.util.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PacketTest {

    @BeforeEach
    void setUp() {
        PacketRegistry.getInstance().register(TestPacket.class, new TestPacketHandler());
    }

    @Test
    void testPacketSerial() {
        BufferWrapper buffer = BufferWrapper.allocate();
        TestPacket packet = new TestPacket();
        packet.testInt = 4;
        Packet.deconstructPacket(packet, buffer);

        assertEquals((byte) 0, buffer.getEncryptionByte());//Encryption byte
        Optional<Packet> optional = Packet.constructPacket(buffer);
        assertTrue(optional.isPresent());
        TestPacket packet2 = (TestPacket) optional.get();

        assertEquals(packet.testInt, packet2.testInt);
    }

    public static class TestPacket extends Packet {

        protected int testInt = 87542;

        @Override
        public void deconstruct(BufferWrapper buf) {
            buf.putInt(testInt);
        }

        @Override
        public void construct(BufferWrapper buf) {
            testInt = buf.getInt();
        }

        @Override
        public String getId() {
            return "test";
        }
    }

    public static class TestPacketHandler implements PacketHandler<TestPacket> {
        @Override
        public void handle(TestPacket packet) {
            Logger.info("Handle TestPacket: " + packet.testInt);
        }
    }
}
