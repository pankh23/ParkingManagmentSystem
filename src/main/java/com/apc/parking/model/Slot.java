package com.apc.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name="slots")
public class Slot {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String type; // "2W" or "4W"
    private boolean isOccupied;
    private Long vehicleId; // which vehicle is parked
}