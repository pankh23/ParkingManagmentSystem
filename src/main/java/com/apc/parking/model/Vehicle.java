package com.apc.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name="vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String type; // "2W" or "4W"
    private String plateNumber;

    @ManyToOne
    private User user;

    private boolean parked;
}