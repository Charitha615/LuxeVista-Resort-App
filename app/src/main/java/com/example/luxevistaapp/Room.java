package com.example.luxevistaapp;

public class Room {
    private int id;
    private String type;
    private String description;
    private double pricePerNight;
    private int capacity;
    private boolean available;
    private String imageUrl;

    public Room() {
    }

    public Room(int id, String type, String description, double pricePerNight,
                int capacity, boolean available, String imageUrl) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.available = available;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}