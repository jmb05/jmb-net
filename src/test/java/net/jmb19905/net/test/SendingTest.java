package net.jmb19905.net.test;

import net.jmb19905.net.Client;
import net.jmb19905.net.Connection;
import net.jmb19905.net.Server;
import net.jmb19905.net.event.ActiveEventListener;
import net.jmb19905.net.event.ReadEventListener;
import net.jmb19905.net.packet.PacketRegistry;
import net.jmb19905.util.Logger;
import net.jmb19905.util.crypto.Encryption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SendingTest {

    Server server;
    Client client;
    Connection connectionServer;
    Connection connectionClient;
    AtomicBoolean b1;
    AtomicBoolean b2;

    @BeforeEach
    void setUp() {
        PacketRegistry.getInstance().register(PacketTest.TestPacket.class, new PacketTest.TestPacketHandler());

        b1 = new AtomicBoolean(false);
        b2 = new AtomicBoolean(false);
        server = new Server();
        connectionServer = server.addTcp(12121);
        connectionServer.getHandler().addEventListener((ActiveEventListener) evt -> {
            Logger.info("Server Channel active...");
            Logger.info("Server Sending TestPacket");
            evt.getContext().send(new PacketTest.TestPacket());
        });
        client = new Client("localhost");
        connectionServer.getHandler().addEventListener((ReadEventListener) evt -> b2.set(true));
        connectionClient = client.addTcp(12121);
        connectionClient.getHandler().addEventListener((ActiveEventListener) evt -> {
            Logger.info("Client Channel active...");
            Logger.info("Client Sending TestPacket");
            evt.getContext().send(new PacketTest.TestPacket());
        });
        connectionClient.getHandler().addEventListener((ReadEventListener) evt -> b1.set(true));
    }

    @Test
    void testSending() {
        server.start();
        client.start();
    }

    @Test
    void testEncryptedSending() {
        Encryption serverEncryption = new Encryption();
        Encryption clientEncryption = new Encryption();
        serverEncryption.setReceiverPublicKey(clientEncryption.getPublicKey());
        clientEncryption.setReceiverPublicKey(serverEncryption.getPublicKey());

        connectionServer.setEncryption(serverEncryption);
        connectionClient.setEncryption(clientEncryption);

        server.start();
        client.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(2000);
        assertTrue(b1.get());
        assertTrue(b2.get());
        client.stop();
        server.stop();
        Thread.sleep(2000);
    }

}
