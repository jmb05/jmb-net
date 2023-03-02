package net.jmb19905.net.buffer;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;
import io.netty5.buffer.DefaultBufferAllocators;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BufferWrapper {

    private static final int DEFAULT_SIZE = 256;
    private static final BufferAllocator allocator = DefaultBufferAllocators.preferredAllocator();
    private final Buffer buffer;

    public BufferWrapper(Buffer buffer) {
        this.buffer = buffer;
        putByte((byte) 0);
    }

    public static BufferWrapper allocate(int size) {
        return new BufferWrapper(allocator.allocate(size));
    }

    public static BufferWrapper allocate() {
        return new BufferWrapper(allocator.allocate(DEFAULT_SIZE));
    }

    public static BufferWrapper allocate(BufferAllocator allocator, int size) {
        return new BufferWrapper(allocator.allocate(size));
    }

    public static BufferWrapper allocate(BufferAllocator allocator) {
        return new BufferWrapper(allocator.allocate(DEFAULT_SIZE));
    }

    public void putString(String s) {
        if (s == null) {
            buffer.writeInt(0);
            return;
        }
        putByte((byte) s.length());
        buffer.writeCharSequence(s, StandardCharsets.UTF_8);
    }

    public String getString() {
        byte length = getByte();
        if (length <= 0) return "";
        return buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
    }

    public String[] getRemainingStrings() {
        List<String> strings = new ArrayList<>();
        while (true) {
            try {
                strings.add(getString());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return strings.toArray(new String[0]);
    }

    public void putInt(int i) {
        buffer.writeInt(i);
    }

    public int getInt() {
        return buffer.readInt();
    }

    public void putLong(long l) {
        buffer.writeLong(l);
    }

    public long getLong() {
        return buffer.readLong();
    }

    public void putDouble(double d) {
        buffer.writeDouble(d);
    }

    public double getDouble() {
        return buffer.readDouble();
    }

    public void putFloat(float f) {
        buffer.writeFloat(f);
    }

    public float getFloat() {
        return buffer.readFloat();
    }

    public void putChar(char c) {
        buffer.writeChar(c);
    }

    public char getChar() {
        return buffer.readChar();
    }

    public void putBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    public boolean getBoolean() {
        return buffer.readBoolean();
    }

    public void putByte(byte b) {
        buffer.writeByte(b);
    }

    public byte getByte() {
        return buffer.readByte();
    }

    public void putUUID(UUID uuid) {
        putLong(uuid.getMostSignificantBits());
        putLong(uuid.getLeastSignificantBits());
    }

    public UUID getUUID() {
        long mostSigBits = getLong();
        long leastSigBits = getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public void putBytes(byte[] bytes) {
        putInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    public byte[] getBytes() {
        int length = getInt();
        byte[] data = new byte[length];
        buffer.readBytes(data, 0, length);
        return data;
    }

    public void put(BufferSerializable obj) {
        obj.deconstruct(this);
    }

    public <T extends BufferSerializable> Optional<T> get(Class<T> objClass) {
        try {
            Constructor<T> constructor = objClass.getConstructor();
            T obj = constructor.newInstance();
            obj.construct(this);
            return Optional.of(obj);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Logger.error(e);
            return Optional.empty();
        }
    }

    public void putArray(BufferSerializable[] array) {
        BufferSerializable[] putArray = Arrays.stream(array).filter(Objects::nonNull).toArray(BufferSerializable[]::new);
        putInt(putArray.length);
        Arrays.stream(putArray).forEach(this::put);
    }

    @SuppressWarnings("unchecked")
    public <T extends BufferSerializable> BufferSerializable[] getArray(Class<T> objClass) {
        int length = getInt();

        T[] objs = (T[]) Array.newInstance(objClass, length);
        for (int i=0;i<length;i++) {
            Optional<T> objOpt = get(objClass);
            if (objOpt.isPresent()) objs[i] = objOpt.get();
        }
        return objs;
    }

    public byte[] toByteArray() {
        int readOffset = buffer.readerOffset();
        buffer.readerOffset(0);
        byte[] rawData = new byte[buffer.readableBytes()];
        buffer.readBytes(rawData, 0, rawData.length);
        buffer.readerOffset(readOffset);
        return rawData;
    }

    public void encrypt(Encryption encryption) {
        if (!encryption.isUsable()) return;
        byte[] fullData = toByteArray();
        byte[] data = Arrays.copyOfRange(fullData, 1, fullData.length);
        data = encryption.encrypt(data);
        Logger.info(new String(data, StandardCharsets.UTF_8));
        buffer.readerOffset(0);
        byte encryptionByte = getByte();
        buffer.readerOffset(0);
        buffer.writerOffset(0);
        putByte((byte) (encryptionByte + 1));
        buffer.writeBytes(data);
    }

    public void decrypt(Encryption encryption) {
        if (!encryption.isUsable()) return;
        if (getEncryptionByte() == 0) return;
        byte[] fullData = toByteArray();
        byte[] data = Arrays.copyOfRange(fullData, 1, fullData.length - 1);
        try {
            Logger.info(new String(data, StandardCharsets.UTF_8));
            data = encryption.decrypt(data);
        } catch (IllegalArgumentException e) {
            Logger.warn(e);
        }
        buffer.readerOffset(0);
        byte encryptionByte = getByte();
        buffer.readerOffset(0);
        buffer.writerOffset(0);
        if (encryptionByte > 0) encryptionByte -= 1;
        putByte(encryptionByte);
        buffer.writeBytes(data);
    }

    public byte getEncryptionByte() {
        buffer.readerOffset(0);
        return getByte();
    }

    public byte checkEncryptionLevel() {
        int readerOffset = buffer.readerOffset();
        buffer.readerOffset(0);
        byte encryptionByte = getByte();
        buffer.readerOffset(readerOffset);
        return encryptionByte;
    }

    public boolean isEncrypted() {
        return checkEncryptionLevel() > 0;
    }

    public int getSize() {
        return buffer.readableBytes();
    }

    public void clear() {
        buffer.readerOffset(0);
        buffer.writerOffset(0);
    }

}