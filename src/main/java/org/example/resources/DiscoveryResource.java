package org.example.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.example.models.ApiResponse;
import org.example.store.DataStore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private final DataStore db = DataStore.getInstance();

    @GET
    public Response getApiMetadata(@Context UriInfo uriInfo) {

        // 1. Generate dynamic links to other endpoints
        String baseUri = uriInfo.getBaseUri().toString();
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", baseUri);
        links.put("rooms", baseUri + "rooms");
        links.put("sensors", baseUri + "sensors");

        Map<String, String> supportInfo = new LinkedHashMap<>();
        supportInfo.put("developer", "Smart Campus Core Team");
        supportInfo.put("contact", "admin.api@smartcampus.local");
        supportInfo.put("status", "System Online");

        int totalRooms = db.getRoomTable().size();
        int totalSensors = db.getSensorTable().size();

        int totalReadings = db.getReadingTable().values().stream()
                .mapToInt(List::size)
                .sum();

        Map<String, Integer> metrics = new LinkedHashMap<>();
        metrics.put("activeRooms", totalRooms);
        metrics.put("registeredSensors", totalSensors);
        metrics.put("totalReadings", totalReadings);

        Map<String, Object> discoveryData = new LinkedHashMap<>();
        discoveryData.put("apiName", "Smart Campus API Core");
        discoveryData.put("version", "1.0.0");
        discoveryData.put("description", "Central gateway for managing rooms, sensors, and telemetry data.");
        discoveryData.put("support", supportInfo);
        discoveryData.put("metrics", metrics);
        discoveryData.put("endpoints", links);

        return Response.ok(new ApiResponse<>("Discovery data retrieved", discoveryData)).build();
    }
}