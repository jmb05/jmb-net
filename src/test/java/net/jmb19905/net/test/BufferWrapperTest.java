package net.jmb19905.net.test;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.util.crypto.Encryption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class BufferWrapperTest {

    BufferWrapper buffer;
    byte[] bytes;
    UUID id;

    @BeforeEach
    void setUp() {
        buffer = BufferWrapper.allocate();
        buffer.putByte((byte) 20);
        buffer.putBoolean(true);
        buffer.putLong(97518763521325378L);
        buffer.putInt(632324);
        bytes = new byte[]{0, 24, 2, 127};
        buffer.putBytes(bytes);
        buffer.putChar('c');
        buffer.putFloat(23.56f);
        buffer.putDouble(435.3451);
        buffer.putString("Hello Test");
        id = UUID.randomUUID();
        buffer.putUUID(id);
    }

    @Test
    @DisplayName("Test putting values into and then getting them out of the buffer")
    void testPutGet() {}

    @Test
    @DisplayName("Tests the encryption and decryption of the buffer")
    void testSingleEncryption() {
        Encryption encryption1 = new Encryption();
        Encryption encryption2 = new Encryption();
        encryption1.setReceiverPublicKey(encryption2.getPublicKey());
        encryption2.setReceiverPublicKey(encryption1.getPublicKey());

        buffer.encrypt(encryption1);
        assertEquals((byte) 1, buffer.getByte());
        buffer.decrypt(encryption2);
    }

    @Test
    @DisplayName("Tests the encryption and decryption of the buffer with multiple layers of encryption")
    void testMultipleEncryption() {
        Encryption encryption1 = new Encryption();
        Encryption encryption2 = new Encryption();
        encryption1.setReceiverPublicKey(encryption2.getPublicKey());
        encryption2.setReceiverPublicKey(encryption1.getPublicKey());

        Encryption encryption3 = new Encryption();
        Encryption encryption4 = new Encryption();
        encryption3.setReceiverPublicKey(encryption4.getPublicKey());
        encryption4.setReceiverPublicKey(encryption3.getPublicKey());

        buffer.encrypt(encryption1);
        assertEquals((byte) 1, buffer.getByte());
        buffer.encrypt(encryption3);
        assertEquals((byte) 2, buffer.getByte());
        buffer.decrypt(encryption4);
        assertEquals((byte) 1, buffer.getByte());
        buffer.decrypt(encryption2);
    }

    @AfterEach
    void tearDown() {
        assertEquals((byte) 0, buffer.getByte()); //Encryption Byte
        assertEquals((byte) 20, buffer.getByte());
        assertTrue(buffer.getBoolean());
        assertEquals(97518763521325378L, buffer.getLong());
        assertEquals(632324, buffer.getInt());
        assertArrayEquals(bytes, buffer.getBytes());
        assertEquals('c', buffer.getChar());
        assertEquals(23.56f, buffer.getFloat());
        assertEquals(435.3451, buffer.getDouble());
        assertEquals("Hello Test", buffer.getString());
        assertEquals(id, buffer.getUUID());
    }

}