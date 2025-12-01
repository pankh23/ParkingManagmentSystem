package com.apc.parking.model;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    private Long id;

    private String type; // "2W" or "4W"
    private String plateNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Make sure you have a User entity

    private boolean parked;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isParked() { return parked; }
    public void setParked(boolean parked) { this.parked = parked; }
}
