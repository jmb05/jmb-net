package net.jmb19905.net;

import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    private final Map<String, Connection> connections = new HashMap<>();
    private boolean running = false;

    public abstract Connection addTcp(int port);

    public Connection addConnection(String name, Connection connection) {
        this.connections.put(name, connection);
        if (running) connection.start();
        return connection;
    }

    public Connection getConnection(String name) {
        return connections.get(name);
    }

    public Map<String, Connection> getConnections() {
        return connections;
    }

    public void start() {
        for (String name : connections.keySet()) {
            Connection connection = connections.get(name);
            if (connection == null) continue;
            connection.start();
        }
        running = true;
    }

    public void stop() {
        for (String name : connections.keySet()) {
            Connection connection = connections.get(name);
            if (connection == null) continue;
            connection.stop();
        }
        running = false;

    }
}
