package org.example.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        LOGGER.info(">>> INCOMING REQUEST: " + method + " /" + path);

        // Start a stopwatch to see how fast your API is
        requestContext.setProperty("startTime", System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();


        long startTime = (long) requestContext.getProperty("startTime");
        long executionTime = System.currentTimeMillis() - startTime;

        LOGGER.info("<<< OUTGOING RESPONSE: " + method + " /" + path + " - Status: " + status + " (" + executionTime + "ms)");
    }
}