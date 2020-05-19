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
    final private int maxConnections;
    final private int port;
    final private String destination;

    public HideMeServer(final String destination, final int port, final int maxConnections) {
        this.destination = destination;
        this.port = port;
        this.maxConnections = maxConnections;
    }

    public void start() {
        log.info("Starting HideMe service");
        ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnections);
        try (ServerSocket server = new ServerSocket(this.port)) {
            log.info("Listening on port {}", this.port);
            while (server.isBound()) {
                final Socket client = server.accept();
                log.debug("New client connected.");
                threads.execute(
                        new ClientConnectionBridge(
                                client,
                                InetAddress.getByName(destination)
                        ));
            }
        } catch (IOException e) {
            log.error("A fatal error has occurred", e);
        } finally {
            log.info("Closing connections...");
            threads.shutdown();
        }
        log.info("Service is shutting down");
    }
}
