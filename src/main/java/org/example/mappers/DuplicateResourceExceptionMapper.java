package org.example.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.DuplicateResourceException;

@Provider
public class DuplicateResourceExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

    @Override
    public Response toResponse(DuplicateResourceException exception) {
        return buildResponse(Response.Status.CONFLICT, exception.getMessage());
    }
}