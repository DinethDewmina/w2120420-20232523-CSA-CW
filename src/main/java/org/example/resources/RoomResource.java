package org.example.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.example.exception.DuplicateResourceException;
import org.example.exception.RoomNotEmptyException;
import org.example.models.ApiResponse;
import org.example.models.Room;
import org.example.store.DataStore;

import java.net.URI;
import java.util.ArrayList;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore db = DataStore.getInstance();

    @GET
    public Response listRooms() {

        return Response.ok(new ApiResponse<>(
                "Rooms retrieved successfully.",
                new ArrayList<>(db.getRoomTable().values())
        )).build();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            throw new BadRequestException("A valid room payload and ID are required.");
        }

        if (db.getRoomTable().containsKey(room.getId())) {
            throw new DuplicateResourceException("A room with ID '" + room.getId() + "' already exists.");
        }

        room.setSensorIds(new ArrayList<>());
        db.getRoomTable().put(room.getId(), room);

        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Room created successfully.", room))
                .build();
    }

    @GET
    @Path("{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRoomTable().get(roomId);
        if (room == null) {
            throw new NotFoundException("Room '" + roomId + "' was not found.");
        }

        return Response.ok(new ApiResponse<>("Room retrieved successfully.", room)).build();
    }

    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRoomTable().get(roomId);
        if (room == null) {
            throw new NotFoundException("Room '" + roomId + "' was not found.");
        }

        if (!room.getSensorIds().isEmpty() || db.hasActiveSensors(roomId)) {
            throw new RoomNotEmptyException("Room '" + roomId + "' cannot be deleted because it contains sensors.");
        }

        db.getRoomTable().remove(roomId);
        return Response.ok(new ApiResponse<>("Room deleted successfully.", null)).build();
    }
}