import java.time.LocalDateTime;

public class Vehicle {
    private String vehicleNumber;
    private String ownerName;
    private String vehicleType; // "2W" or "4W"
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double parkingCharges;

    public Vehicle(String vehicleNumber, String ownerName, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
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

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getParkingCharges() {
        return parkingCharges;
    }

    public void setParkingCharges(double parkingCharges) {
        this.parkingCharges = parkingCharges;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleNumber='" + vehicleNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", parkingCharges=" + parkingCharges +
                '}';
    }
} 