package com.nikwithak.hideme;

import mockit.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HideMeServerTest {
    final static int PORT = 8443;
    final static String DESTINATION = "api.giphy.com";
    final static int MAX_CONN = 30;

    @Mocked ServerSocket server;
    @Mocked Socket socket;
    @Mocked ClientConnectionBridge clientConnectionBridge;
    @Tested HideMeServer hideMeServer = new HideMeServer(DESTINATION, PORT, MAX_CONN);

    @Test
    public void testServerSetupAndTeardown_success() throws Exception {
        new Expectations() {{
            server.isBound(); result = false;
        }};

        hideMeServer.start();

        new Verifications() {{
            server.close(); times = 1;
        }};
    }

    @Test
    public void testIOException_cleanShutdownOnError() throws Exception {
        new Expectations() {{
            server.isBound(); result = true;
            server.accept(); result = new IOException();
        }};

        hideMeServer.start();

        new Verifications() {{
            server.close(); times = 1;
        }};
    }

    @Test
    public void testMultipleClientsConnected() throws Exception {
        new Expectations() {{
            server.isBound(); returns(true, true, true, true, true, false);
        }};

        hideMeServer.start();

        new Verifications() {{
            List<ClientConnectionBridge> clients =
                    withCapture(new ClientConnectionBridge(withAny(socket), InetAddress.getByName(DESTINATION)));

            Assert.assertEquals(5, clients.size());
            clients.forEach(c -> { c.run(); times = 1; });
        }};
    }
}
