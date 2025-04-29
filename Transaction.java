import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Transaction {
    private Vehicle vehicle;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double charges;

    public Transaction(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.entryTime = vehicle.getEntryTime();
        this.exitTime = null;
        this.charges = 0.0;
    }

    public void completeTransaction() {
        this.exitTime = LocalDateTime.now();
        calculateCharges();
    }

    private void calculateCharges() {
        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
        double baseRate = vehicle.getVehicleType().equals("2W") ? 20.0 : 40.0;
        this.charges = baseRate * Math.max(1, hours);
        vehicle.setParkingCharges(charges);
    }

    public double getCharges() {
        return charges;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "vehicleNumber='" + vehicle.getVehicleNumber() + '\'' +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", charges=" + charges +
                '}';
    }
} 