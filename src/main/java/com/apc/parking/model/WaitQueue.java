package com.apc.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name="wait_queue")
public class WaitQueue {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    private String slotType; // 2W or 4W
    private long timestamp; // when it entered queue
}