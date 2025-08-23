public class ParkingSlot {
    private int slotNumber;
    private boolean isOccupied;
    private Vehicle parkedVehicle;
    private String slotType; 

    public ParkingSlot(int slotNumber, String slotType) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public boolean parkVehicle(Vehicle vehicle) {
        if (!isOccupied && vehicle.getVehicleType().equals(slotType)) {
            this.parkedVehicle = vehicle;
            this.isOccupied = true;
            return true;
        }
        return false;
    }

    public Vehicle removeVehicle() {
        if (isOccupied) {
            Vehicle vehicle = parkedVehicle;
            parkedVehicle = null;
            isOccupied = false;
            return vehicle;
        }
        return null;
    }

    
    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public String getSlotType() {
        return slotType;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotNumber=" + slotNumber +
                ", isOccupied=" + isOccupied +
                ", slotType='" + slotType + '\'' +
                ", parkedVehicle=" + (parkedVehicle != null ? parkedVehicle.getVehicleNumber() : "None") +
                '}';
    }
} 