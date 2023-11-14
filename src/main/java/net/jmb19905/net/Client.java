package net.jmb19905.net;

import net.jmb19905.net.tcp.ClientTcpThread;

public class Client extends Endpoint {

    private final String server;

    public Client(String server) {
        this.server = server;
    }

    @Override
    public NetThread addTcp(int port) {
        return addThread("client_tcp", new ClientTcpThread(server, port));
    }
}
