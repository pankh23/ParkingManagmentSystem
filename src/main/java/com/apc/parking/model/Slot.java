package com.apc.parking.model;

import javax.persistence.*;

@Entity
@Table(name = "slots")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number")
    private String slotNumber; // e.g., "A1", "B2", etc.

    @Column(name = "slot_type")
    private String type; // "2W" or "4W"
    
    private boolean occupied;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Version
    private Long version; // For optimistic locking

    // Constructors
    public Slot() {}

    public Slot(String slotNumber, String type, ParkingLot parkingLot) {
        this.slotNumber = slotNumber;
        this.type = type;
        this.parkingLot = parkingLot;
        this.occupied = false;
    }

    // Getters & Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getType() { 
        return type; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }

    public boolean isOccupied() { 
        return occupied; 
    }
    
    public void setOccupied(boolean occupied) { 
        this.occupied = occupied; 
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Vehicle getVehicle() { 
        return vehicle; 
    }
    
    public void setVehicle(Vehicle vehicle) { 
        this.vehicle = vehicle; 
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                ", slotNumber='" + slotNumber + '\'' +
                ", type='" + type + '\'' +
                ", occupied=" + occupied +
                ", parkingLot=" + (parkingLot != null ? parkingLot.getName() : "null") +
                '}';
    }
}
