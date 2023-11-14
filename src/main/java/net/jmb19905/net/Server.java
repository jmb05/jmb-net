package net.jmb19905.net;

import net.jmb19905.net.tcp.ServerTcpThread;

public class Server extends Endpoint {
    @Override
    public NetThread addTcp(int port) {
        return addThread("server_tcp", new ServerTcpThread(port));
    }
}
