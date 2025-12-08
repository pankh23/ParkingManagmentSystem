package com.apc.parking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "parking_lots")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "total_slots_2w")
    private Integer totalSlots2W = 0;

    @Column(name = "total_slots_4w")
    private Integer totalSlots4W = 0;

    @Column(name = "price_per_hour_2w")
    private Double pricePerHour2W = 10.0;

    @Column(name = "price_per_hour_4w")
    private Double pricePerHour4W = 20.0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Slot> slots;

    // Constructors
    public ParkingLot() {}

    public ParkingLot(String name, String location, Integer totalSlots2W, Integer totalSlots4W) {
        this.name = name;
        this.location = location;
        this.totalSlots2W = totalSlots2W;
        this.totalSlots4W = totalSlots4W;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalSlots2W() {
        return totalSlots2W;
    }

    public void setTotalSlots2W(Integer totalSlots2W) {
        this.totalSlots2W = totalSlots2W;
    }

    public Integer getTotalSlots4W() {
        return totalSlots4W;
    }

    public void setTotalSlots4W(Integer totalSlots4W) {
        this.totalSlots4W = totalSlots4W;
    }

    public Double getPricePerHour2W() {
        return pricePerHour2W;
    }

    public void setPricePerHour2W(Double pricePerHour2W) {
        this.pricePerHour2W = pricePerHour2W;
    }

    public Double getPricePerHour4W() {
        return pricePerHour4W;
    }

    public void setPricePerHour4W(Double pricePerHour4W) {
        this.pricePerHour4W = pricePerHour4W;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    // Helper methods
    public Integer getTotalSlots() {
        return (totalSlots2W != null ? totalSlots2W : 0) + (totalSlots4W != null ? totalSlots4W : 0);
    }

    public Integer getAvailableSlots2W() {
        if (slots == null) return totalSlots2W;
        return (int) slots.stream()
                .filter(slot -> "2W".equals(slot.getType()) && !slot.isOccupied())
                .count();
    }

    public Integer getAvailableSlots4W() {
        if (slots == null) return totalSlots4W;
        return (int) slots.stream()
                .filter(slot -> "4W".equals(slot.getType()) && !slot.isOccupied())
                .count();
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", totalSlots2W=" + totalSlots2W +
                ", totalSlots4W=" + totalSlots4W +
                ", isActive=" + isActive +
                '}';
    }
}
