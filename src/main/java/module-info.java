module jmb.net {
    requires io.netty5.transport;
    requires jmb.utilities;
    requires io.netty5.common;
    requires org.jetbrains.annotations;
    requires io.netty5.buffer;

    exports net.jmb19905.net;
    exports net.jmb19905.net.buffer;
    exports net.jmb19905.net.event;
    exports net.jmb19905.net.handler;
    exports net.jmb19905.net.packet;
    exports net.jmb19905.net.tcp;
}