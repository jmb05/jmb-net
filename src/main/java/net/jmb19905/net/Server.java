package net.jmb19905.net;

import net.jmb19905.net.tcp.ServerTcpConnection;

public class Server extends Endpoint {
    @Override
    public Connection addTcp(int port) {
        return addConnection("server_tcp", new ServerTcpConnection(port));
    }
}
