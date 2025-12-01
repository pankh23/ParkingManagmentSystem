//package com.apc.parking.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name="wait_queue")
//public class WaitQueue {
//
//    @Id
//    @GeneratedValue(strategy=GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    private Vehicle vehicle;
//
//    private String slotType; // 2W or 4W
//    private long timestamp; // when it entered queue
//}

package com.apc.parking.model;

import javax.persistence.*;

@Entity
@Table(name="wait_queue")
public class WaitQueue {

    @Id
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    private String slotType; // "2W" or "4W"
    private long timestamp;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getSlotType() {
        return slotType;
    }
    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
