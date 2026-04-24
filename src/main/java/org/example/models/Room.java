package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String id;
    private String name;
    private int capacity;

    // This tracks which sensors belong to this room
    private List<String> sensorIds = new ArrayList<>();

    // Required by Jackson for JSON conversion
    public Room() {
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        // Safe check to ensure the list is never null
        this.sensorIds = (sensorIds == null) ? new ArrayList<>() : sensorIds;
    }

}