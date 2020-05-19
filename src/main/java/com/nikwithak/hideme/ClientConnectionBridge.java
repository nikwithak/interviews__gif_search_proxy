package com.nikwithak.hideme;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class spawns two new threads to bridge a connection between a client and the specified destination.
 */
@Slf4j
public class ClientConnectionBridge implements Runnable {
    private Socket client;
    private InetAddress destination;
    private int destinationPort;

    private Thread clientToDest;
    private Thread destToClient;

    public ClientConnectionBridge(Socket client, InetAddress dest) {
        this(client, dest, 443);
    }

    public ClientConnectionBridge(Socket client, InetAddress dest, int port) {
        this.client = client;
        this.destination = dest;
        this.destinationPort = port;
    }

    public void run() {
        try (Socket client = this.client; Socket dest = new Socket(destination, destinationPort)) {
            (clientToDest = new Thread(new IOStreamCoupler(
                    new BufferedInputStream(client.getInputStream()),
                    new BufferedOutputStream(dest.getOutputStream())
            ))).start();
            (destToClient = new Thread(new IOStreamCoupler(
                    new BufferedInputStream(dest.getInputStream()),
                    new BufferedOutputStream(client.getOutputStream())
            ))).start();

            clientToDest.join();
            destToClient.join();
        } catch (IOException | InterruptedException e) {
            log.error("An exception ({}) occurred while bridging a connection in ClientConnectionBridge.",
                    e.getClass().getName());
        }
    }
}
