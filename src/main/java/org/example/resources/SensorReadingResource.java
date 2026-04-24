package org.example.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.example.models.ApiResponse;
import org.example.models.SensorReading;
import org.example.store.DataStore;

import java.net.URI;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore db = DataStore.getInstance();

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response listReadings() {

        if (!db.getSensorTable().containsKey(sensorId)) {
            throw new NotFoundException("Sensor '" + sensorId + "' not found.");
        }

        List<SensorReading> readings = db.getReadingTable().getOrDefault(sensorId, List.of());

        return Response.ok(new ApiResponse<>("Readings retrieved successfully.", readings)).build();
    }

    @POST
    public Response createReading(SensorReading reading, @Context UriInfo uriInfo) {

        SensorReading createdReading = db.addReading(sensorId, reading);

        URI location = uriInfo.getAbsolutePathBuilder().path(createdReading.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Reading created successfully and sensor currentValue updated.", createdReading))
                .build();
    }

    @GET
    @Path("{readingId}")
    public Response getReading(@PathParam("readingId") String readingId) {
        if (!db.getSensorTable().containsKey(sensorId)) {
            throw new NotFoundException("Sensor '" + sensorId + "' not found.");
        }

        List<SensorReading> readings = db.getReadingTable().getOrDefault(sensorId, List.of());

        SensorReading foundReading = readings.stream()
                .filter(r -> r.getId().equals(readingId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Reading '" + readingId + "' not found."));

        return Response.ok(new ApiResponse<>("Reading retrieved successfully.", foundReading)).build();
    }
}