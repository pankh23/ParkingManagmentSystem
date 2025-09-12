package com.apc.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    private Long slotId;
    private String action; // "PARKED" or "EXITED"
    private long timestamp;
}