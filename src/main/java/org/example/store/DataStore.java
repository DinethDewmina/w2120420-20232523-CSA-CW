package org.example.store;

import org.example.exception.*;
import org.example.models.Room;
import org.example.models.Sensor;
import org.example.models.SensorReading;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataStore {

    private static final DataStore instance = new DataStore();
    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "MAINTENANCE", "OFFLINE");


    private final Map<String, Room> roomTable = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensorTable = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readingTable = new ConcurrentHashMap<>();

    private DataStore() {}

    public static DataStore getInstance() {
        return instance;
    }

    public Map<String, Room> getRoomTable() { return roomTable; }
    public Map<String, Sensor> getSensorTable() { return sensorTable; }
    public Map<String, List<SensorReading>> getReadingTable() { return readingTable; }

    public Sensor createSensor(Sensor incoming) {
        if (incoming == null || incoming.getId() == null || incoming.getId().isBlank()) {
            throw new BadRequestException("A valid sensor payload and ID are required.");
        }

        if (sensorTable.containsKey(incoming.getId())) {
            throw new DuplicateResourceException("Sensor ID '" + incoming.getId() + "' already exists.");
        }

        Room room = roomTable.get(incoming.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Cannot link to Room '" + incoming.getRoomId() + "' because it does not exist.");
        }

        // Validate Status
        String status = (incoming.getStatus() == null || incoming.getStatus().isBlank()) ? "ACTIVE" : incoming.getStatus().toUpperCase();
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new BadRequestException("Status must be ACTIVE, MAINTENANCE, or OFFLINE.");
        }
        incoming.setStatus(status);

        // Save
        sensorTable.put(incoming.getId(), incoming);
        readingTable.put(incoming.getId(), new ArrayList<>()); // initialize readings list
        room.getSensorIds().add(incoming.getId());

        return incoming;
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = sensorTable.get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor '" + sensorId + "' not found.");
        }

        if (!"ACTIVE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor '" + sensorId + "' is " + sensor.getStatus() + ". Cannot accept readings.");
        }

        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        readingTable.get(sensorId).add(reading);
        sensor.setCurrentValue(reading.getValue());

        return reading;
    }

    public boolean hasActiveSensors(String roomId) {
        return sensorTable.values().stream().anyMatch(s -> roomId.equals(s.getRoomId()));
    }
}