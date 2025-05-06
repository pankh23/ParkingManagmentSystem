import java.time.LocalDateTime;
import java.time.Duration;

public class Vehicle {
    private String vehicleNumber;
    private String ownerName;
    private String vehicleType; // "2W" or "4W"
    private LocalDateTime entryTime;
    private String parkingSlot;
    private double parkingCharges;

    public Vehicle(String vehicleNumber, String ownerName, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.parkingCharges = 0.0;
    }

    // Getters and Setters
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getParkingSlot() {
        return parkingSlot;
    }

    public void setParkingSlot(String parkingSlot) {
        this.parkingSlot = parkingSlot;
    }

    public double getParkingCharges() {
        return parkingCharges;
    }

    public void setParkingCharges(double charges) {
        this.parkingCharges = charges;
    }

    public long getParkingDuration() {
        return Duration.between(entryTime, LocalDateTime.now()).toMinutes();
    }

    @Override
    public String toString() {
        return "Vehicle Number: " + vehicleNumber + 
               "\nOwner Name: " + ownerName + 
               "\nVehicle Type: " + vehicleType + 
               "\nEntry Time: " + entryTime + 
               "\nParking Slot: " + (parkingSlot != null ? parkingSlot : "Not parked") +
               "\nCurrent Duration: " + getParkingDuration() + " minutes" +
               "\nParking Charges: Rs. " + parkingCharges;
    }
} 