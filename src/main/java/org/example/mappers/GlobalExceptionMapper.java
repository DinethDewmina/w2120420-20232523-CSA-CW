package org.example.mappers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = 500;
        String message = exception.getMessage();

        if (exception instanceof WebApplicationException) {
            status = ((WebApplicationException) exception).getResponse().getStatus();
        }

        String jsonResponse = "{\n" +
                "  \"statusCode\": " + status + ",\n" +
                "  \"errorMessage\": \"API Error: " + message + "\"\n" +
                "}";

        return Response.status(status)
                .header("Content-Type", "application/json")
                .entity(jsonResponse)
                .build();
    }
}