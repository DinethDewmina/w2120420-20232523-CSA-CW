package org.example;

import org.glassfish.grizzly.http.server.HttpServer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.INFO);

        HttpServer server = ServerManager.startServer();

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        System.out.println("==================================================");
        System.out.println("Smart Campus API successfully initialized.");
        System.out.println("Discovery Endpoint: http://localhost:8080/api/v1");
        System.out.println("Press Ctrl+C in the terminal to shut down.");
        System.out.println("==================================================");
        Thread.currentThread().join();
    }
}