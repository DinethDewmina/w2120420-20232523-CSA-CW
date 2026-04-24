package org.example.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.example.models.ApiResponse;
import org.example.models.Sensor;
import org.example.store.DataStore;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore db = DataStore.getInstance();

    @GET
    public Response listSensors(@QueryParam("type") String type) {
        List<Sensor> sensors;

        if (type != null && !type.isBlank()) {
            sensors = db.getSensorTable().values().stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        } else {
            sensors = List.copyOf(db.getSensorTable().values());
        }

        String message = (type == null || type.isBlank()) ? "Sensors retrieved successfully." : "Sensors filtered by type retrieved successfully.";
        return Response.ok(new ApiResponse<>(message, sensors)).build();
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {

        if (sensor.getRoomId() == null || !db.getRoomTable().containsKey(sensor.getRoomId())) {
            throw new WebApplicationException(
                    "Cannot create sensor. Room ID '" + sensor.getRoomId() + "' does not exist.",
                    Response.Status.NOT_FOUND
            );
        }

        Sensor createdSensor = db.createSensor(sensor);

        URI location = uriInfo.getAbsolutePathBuilder().path(createdSensor.getId()).build();
        return Response.created(location)
                .entity(new ApiResponse<>("Sensor created successfully.", createdSensor))
                .build();
    }

    @GET
    @Path("{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = db.getSensorTable().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor '" + sensorId + "' was not found.");
        }

        return Response.ok(new ApiResponse<>("Sensor retrieved successfully.", sensor)).build();
    }

    @Path("{sensorId}/readings")
    public SensorReadingResource sensorReadings(@PathParam("sensorId") String sensorId) {
        if (!db.getSensorTable().containsKey(sensorId)) {
            throw new NotFoundException("Sensor '" + sensorId + "' was not found.");
        }

        return new SensorReadingResource(sensorId);
    }
}