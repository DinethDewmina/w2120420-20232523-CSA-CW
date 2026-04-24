package org.example.mappers;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper implements ExceptionMapper<NotSupportedException> {

    @Override
    public Response toResponse(NotSupportedException exception) {
        // This will prove in IntelliJ that we finally caught it!
        System.out.println("DEBUG: Sniper Mapper caught the stubborn 415!");

        // The unbreakable raw string JSON
        String jsonBody = "{\n" +
                "  \"statusCode\": 415,\n" +
                "  \"errorMessage\": \"API Error: Unsupported Media Type. Please send application/json.\"\n" +
                "}";

        return Response.status(415)
                .header("Content-Type", "application/json") // Forces Postman to read it as JSON
                .entity(jsonBody)
                .build();
    }
}