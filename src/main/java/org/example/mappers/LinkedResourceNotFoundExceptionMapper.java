package org.example.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.LinkedResourceNotFoundException;

@Provider
public class LinkedResourceNotFoundExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
    }
}