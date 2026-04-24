package org.example.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.SensorUnavailableException;

@Provider
public class SensorUnavailableExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        return buildResponse(Response.Status.FORBIDDEN, exception.getMessage());
    }
}