package org.example.mappers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String message = exception.getMessage();

        String jsonBody = "{\n" +
                "  \"statusCode\": " + status + ",\n" +
                "  \"errorMessage\": \"API Framework Error: " + message + "\"\n" +
                "}";

        return Response.status(status)
                .header("Content-Type", "application/json")
                .entity(jsonBody)
                .build();
    }
}