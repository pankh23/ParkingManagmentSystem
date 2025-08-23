import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String vehicleNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double charges;
    private String paymentStatus;

    public Transaction(String vehicleNumber, LocalDateTime entryTime, LocalDateTime exitTime, double charges) {
        this.transactionId = UUID.randomUUID().toString();
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.charges = charges;
        this.paymentStatus = exitTime != null ? "Paid" : "Ongoing";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public double getCharges() {
        return charges;
    }

    public long getDuration() {
        if (exitTime == null) {
            return Duration.between(entryTime, LocalDateTime.now()).toMinutes();
        }
        return Duration.between(entryTime, exitTime).toMinutes();
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String status) {
        this.paymentStatus = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction ID: ").append(transactionId)
          .append("\nVehicle Number: ").append(vehicleNumber)
          .append("\nEntry Time: ").append(entryTime)
          .append("\nExit Time: ").append(exitTime != null ? exitTime : "Still Parked")
          .append("\nDuration: ").append(getDuration()).append(" minutes")
          .append("\nCharges: Rs. ").append(charges)
          .append("\nPayment Status: ").append(paymentStatus);
        return sb.toString();
    }
} 