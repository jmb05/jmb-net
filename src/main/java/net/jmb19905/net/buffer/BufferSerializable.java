package net.jmb19905.net.buffer;

public interface BufferSerializable {
    void deconstruct(BufferWrapper buf);
    void construct(BufferWrapper buf);
}
