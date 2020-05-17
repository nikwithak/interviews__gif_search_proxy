package com.nikwithak.hideme;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    /**
     * Loads a .properties file into memory.
     * @param filename The filename of the file to load.
     * @return A {@link Properties} object containing the property data.
     * @throws IOException
     */
    private static Properties loadProperties(final String filename) throws IOException {
        try (final FileInputStream file = new FileInputStream(filename)) {
            Properties props = new Properties();
            props.load(file);
            return props;
        }
    }

    public static void main(String[] args) throws Exception {
        Properties config = loadProperties("config.properties");
        final String destination = config.getProperty("destination");
        final int port = Integer.parseInt(config.getProperty("port"));
        final int maxConnections = Integer.parseInt(config.getProperty("max_connections"));

        HideMeServer server = new HideMeServer(destination, port, maxConnections);
        server.start();
    }
}