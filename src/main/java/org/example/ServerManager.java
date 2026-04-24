package org.example;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties; // Necessary import for the magic bullet

import java.net.URI;

public class ServerManager {

    public static final String BASE_URI = "http://0.0.0.0:8080/api/v1/";

    public static HttpServer startServer() {

        ResourceConfig config = new ResourceConfig()
                .packages(
                        "org.example.resources",
                        "org.example.exceptions",
                        "org.example.mappers"
                );

        config.register(org.glassfish.jersey.jackson.JacksonFeature.class);

        config.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
}