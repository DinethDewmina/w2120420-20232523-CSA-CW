package org.example.mappers;

import jakarta.ws.rs.core.Response;
import org.example.models.ApiError;


public abstract class BaseExceptionMapper {

    // Takes a standard HTTP status and custom message
    protected Response buildResponse(Response.Status status, String message) {
        ApiError error = new ApiError(status.getStatusCode(), message);
        return Response.status(status).entity(error).build();
    }
}