import java.util.*;

public class ParkingLot {
    private List<ParkingSlot> parkingSlots;
    private Map<String, Vehicle> vehicleMap; 
    private Map<String, List<Vehicle>> ownerMap; 
    private Queue<Vehicle> waitlist;
    private List<Transaction> transactionHistory;
    private int twoWheelerSlots;
    private int fourWheelerSlots;

    public ParkingLot(int twoWheelerSlots, int fourWheelerSlots) {
        this.twoWheelerSlots = twoWheelerSlots;
        this.fourWheelerSlots = fourWheelerSlots;
        this.parkingSlots = new ArrayList<>();
        this.vehicleMap = new HashMap<>();
        this.ownerMap = new HashMap<>();
        this.waitlist = new LinkedList<>();
        this.transactionHistory = new ArrayList<>();

        initializeSlots();
    }

    private void initializeSlots() {
        int slotNumber = 1;
        for (int i = 0; i < twoWheelerSlots; i++) {
            parkingSlots.add(new ParkingSlot(slotNumber++, "2W"));
        }
        for (int i = 0; i < fourWheelerSlots; i++) {
            parkingSlots.add(new ParkingSlot(slotNumber++, "4W"));
        }
    }

    public boolean parkVehicle(Vehicle vehicle) {
        // Check if vehicle is already parked
        if (vehicleMap.containsKey(vehicle.getVehicleNumber())) {
            return false;
        }

        // Try to find an available slot
        for (ParkingSlot slot : parkingSlots) {
            if (slot.parkVehicle(vehicle)) {
                vehicleMap.put(vehicle.getVehicleNumber(), vehicle);
                ownerMap.computeIfAbsent(vehicle.getOwnerName(), k -> new ArrayList<>()).add(vehicle);
                transactionHistory.add(new Transaction(vehicle));
                return true;
            }
        }

        // If no slot available, add to waitlist
        waitlist.add(vehicle);
        return false;
    }

    public Vehicle exitVehicle(String vehicleNumber) {
        Vehicle vehicle = vehicleMap.get(vehicleNumber);
        if (vehicle == null) {
            return null;
        }

        // Find and free the parking slot
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getParkedVehicle() != null && 
                slot.getParkedVehicle().getVehicleNumber().equals(vehicleNumber)) {
                slot.removeVehicle();
                break;
            }
        }

        // Complete transaction
        Transaction transaction = transactionHistory.stream()
            .filter(t -> t.getVehicle().getVehicleNumber().equals(vehicleNumber) && t.getExitTime() == null)
            .findFirst()
            .orElse(null);
        
        if (transaction != null) {
            transaction.completeTransaction();
        }

        // Remove from maps
        vehicleMap.remove(vehicleNumber);
        ownerMap.get(vehicle.getOwnerName()).remove(vehicle);
        if (ownerMap.get(vehicle.getOwnerName()).isEmpty()) {
            ownerMap.remove(vehicle.getOwnerName());
        }

        // Check waitlist
        if (!waitlist.isEmpty()) {
            Vehicle nextVehicle = waitlist.poll();
            parkVehicle(nextVehicle);
        }

        return vehicle;
    }

    public List<Vehicle> searchByOwnerName(String ownerName) {
        return ownerMap.getOrDefault(ownerName, new ArrayList<>());
    }

    public Vehicle searchByVehicleNumber(String vehicleNumber) {
        return vehicleMap.get(vehicleNumber);
    }

    public List<Vehicle> getSortedVehiclesByTime() {
        List<Vehicle> vehicles = new ArrayList<>(vehicleMap.values());
        vehicles.sort(Comparator.comparing(Vehicle::getEntryTime));
        return vehicles;
    }

    public List<Vehicle> getSortedVehiclesByCharges() {
        List<Vehicle> vehicles = new ArrayList<>(vehicleMap.values());
        vehicles.sort(Comparator.comparing(Vehicle::getParkingCharges));
        return vehicles;
    }

    public List<ParkingSlot> getAvailableSlots() {
        List<ParkingSlot> available = new ArrayList<>();
        for (ParkingSlot slot : parkingSlots) {
            if (!slot.isOccupied()) {
                available.add(slot);
            }
        }
        return available;
    }

    public Queue<Vehicle> getWaitlist() {
        return waitlist;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public int getAvailableTwoWheelerSlots() {
        return (int) parkingSlots.stream()
                .filter(slot -> slot.getSlotType().equals("2W") && !slot.isOccupied())
                .count();
    }

    public int getAvailableFourWheelerSlots() {
        return (int) parkingSlots.stream()
                .filter(slot -> slot.getSlotType().equals("4W") && !slot.isOccupied())
                .count();
    }
} 