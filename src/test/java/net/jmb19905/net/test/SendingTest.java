package net.jmb19905.net.test;

import net.jmb19905.net.Client;
import net.jmb19905.net.NetThread;
import net.jmb19905.net.Server;
import net.jmb19905.net.event.ActiveEventListener;
import net.jmb19905.net.event.ReadEventListener;
import net.jmb19905.net.packet.PacketRegistry;
import net.jmb19905.net.tcp.ClientTcpThread;
import net.jmb19905.net.tcp.ServerTcpThread;
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
    NetThread netThreadServer;
    NetThread netThreadClient;
    AtomicBoolean b1;
    AtomicBoolean b2;

    @BeforeEach
    void setUp() {
        PacketRegistry.getInstance().register(PacketTest.TestPacket.class, new PacketTest.TestPacketHandler());

        b1 = new AtomicBoolean(false);
        b2 = new AtomicBoolean(false);
        server = new Server();
        netThreadServer = server.addTcp(12121);
        ((ServerTcpThread) netThreadServer).addDefaultEventListener((ActiveEventListener) evt -> {
            Logger.info("Server Channel active...");
            Logger.info("Server Sending TestPacket");
            evt.getContext().send(new PacketTest.TestPacket());
        });
        client = new Client("localhost");
        ((ServerTcpThread) netThreadServer).addDefaultEventListener((ReadEventListener) evt -> b2.set(true));
        netThreadClient = client.addTcp(12121);
        ((ClientTcpThread) netThreadClient).addEventListener((ActiveEventListener) evt -> {
            Logger.info("Client Channel active...");
            Logger.info("Client Sending TestPacket");
            evt.getContext().send(new PacketTest.TestPacket());
        });
        ((ClientTcpThread) netThreadClient).getHandler().addEventListener((ReadEventListener) evt -> b1.set(true));
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

        ((ServerTcpThread) netThreadServer).addDefaultEventListener((ActiveEventListener) l -> netThreadServer.setEncryption(l.getContext().remoteAddress(), serverEncryption));
        ((ClientTcpThread) netThreadClient).setEncryption(clientEncryption);

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
