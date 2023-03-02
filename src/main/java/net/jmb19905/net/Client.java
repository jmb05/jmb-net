package net.jmb19905.net;

import net.jmb19905.net.tcp.ClientTcpConnection;

public class Client extends Endpoint {

    private final String server;

    public Client(String server) {
        this.server = server;
    }

    @Override
    public Connection addTcp(int port) {
        return addConnection("client_tcp", new ClientTcpConnection(server, port));
    }
}
