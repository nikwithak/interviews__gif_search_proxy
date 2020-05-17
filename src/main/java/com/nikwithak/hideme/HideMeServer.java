package com.nikwithak.hideme;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class HideMeServer {
    final private ThreadPoolExecutor threads;
    final private int port;
    final private String destination;

    public HideMeServer(final String destination, final int port, final int maxConnections) throws IOException {
        this.destination = destination;
        this.port = port;
        threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnections);
    }

    public void start() throws Exception {
        log.info("Starting HideMe service");
        try (ServerSocket server = new ServerSocket(this.port)) {
            log.info("Listening on port %d\n", this.port);
            while (server.isBound()) {
                final Socket client = server.accept();
                threads.execute(
                    new ClientConnectionBridge(
                        client,
                        InetAddress.getByName("api.giphy.com")
                ));
            }
        }
    }
}
