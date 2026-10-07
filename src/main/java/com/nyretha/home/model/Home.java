package com.nyretha.home.model;

import org.bukkit.Location;
import java.util.UUID;

public class Home {
    private final UUID owner;
    private final int id;
    private String name;
    private Location location;

    public Home(UUID owner, int id, String name, Location location) {
        this.owner = owner;
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public UUID getOwner() { return owner; }
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}
