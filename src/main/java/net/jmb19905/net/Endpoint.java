package net.jmb19905.net;

import java.util.HashMap;
import java.util.Map;

public abstract class Endpoint {

    private final Map<String, NetThread> connections = new HashMap<>();
    private boolean running = false;

    public abstract NetThread addTcp(int port);

    public NetThread addThread(String name, NetThread netThread) {
        this.connections.put(name, netThread);
        if (running) netThread.start();
        return netThread;
    }

    public NetThread getThread(String name) {
        return connections.get(name);
    }

    public Map<String, NetThread> getThreads() {
        return connections;
    }

    public void start() {
        for (String name : connections.keySet()) {
            NetThread netThread = connections.get(name);
            if (netThread == null) continue;
            netThread.start();
        }
        running = true;
    }

    public void stop() {
        for (String name : connections.keySet()) {
            NetThread netThread = connections.get(name);
            if (netThread == null) continue;
            netThread.stop();
        }
        running = false;

    }
}
