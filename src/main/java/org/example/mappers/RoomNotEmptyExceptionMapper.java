package org.example.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.RoomNotEmptyException;

@Provider
public class RoomNotEmptyExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        return buildResponse(Response.Status.CONFLICT, exception.getMessage());
    }
}