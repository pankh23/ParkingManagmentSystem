import java.util.*;
import java.time.LocalDateTime;

public class ParkingLot {
    private List<ParkingSlot> parkingSlots;
    private Map<String, Vehicle> vehicleMap; 
    private Map<String, List<Vehicle>> ownerMap; 
    private Queue<Vehicle> waitlist;
    private List<Transaction> transactionHistory;
    private int twoWheelerSlots;
    private int fourWheelerSlots;

    public ParkingLot(int twoWheelerSlots, int fourWheelerSlots) {
        if (twoWheelerSlots < 0 || fourWheelerSlots < 0) {
            throw new IllegalArgumentException("Number of slots cannot be negative");
        }
        
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
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        
        if (vehicleMap.containsKey(vehicle.getVehicleNumber())) {
            return false;
        }

        
        for (ParkingSlot slot : parkingSlots) {
            if (slot.parkVehicle(vehicle)) {
                
                vehicle.setParkingSlot(slot.getSlotType() + slot.getSlotNumber());
                vehicleMap.put(vehicle.getVehicleNumber(), vehicle);
                ownerMap.computeIfAbsent(vehicle.getOwnerName(), k -> new ArrayList<>()).add(vehicle);
                
                transactionHistory.add(new Transaction(
                    vehicle.getVehicleNumber(),
                    vehicle.getEntryTime(),
                    null,
                    0.0
                ));
                return true;
            }
        }

        
        waitlist.add(vehicle);
        return false;
    }

    public Vehicle exitVehicle(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty");
        }

        Vehicle vehicle = vehicleMap.get(vehicleNumber);
        if (vehicle == null) {
            return null;
        }

        
        boolean slotFound = false;
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getParkedVehicle() != null && 
                slot.getParkedVehicle().getVehicleNumber().equals(vehicleNumber)) {
                slot.removeVehicle();
                slotFound = true;
                break;
            }
        }

        if (!slotFound) {
            throw new IllegalStateException("Vehicle found in map but not in any parking slot");
        }

        
        LocalDateTime exitTime = LocalDateTime.now();
        double baseRate = vehicle.getVehicleType().equals("2W") ? 20.0 : 40.0;
        long hours = java.time.Duration.between(vehicle.getEntryTime(), exitTime).toHours();
        double charges = baseRate * Math.max(1, hours);
        vehicle.setParkingCharges(charges);

        
        Transaction transaction = transactionHistory.stream()
            .filter(t -> t.getVehicleNumber().equals(vehicleNumber) && t.getExitTime() == null)
            .findFirst()
            .orElse(null);
        
        if (transaction != null) {
            transactionHistory.remove(transaction);
            transactionHistory.add(new Transaction(
                vehicleNumber,
                vehicle.getEntryTime(),
                exitTime,
                charges
            ));
        }

        // Remove from maps
        vehicleMap.remove(vehicleNumber);
        List<Vehicle> ownerVehicles = ownerMap.get(vehicle.getOwnerName());
        if (ownerVehicles != null) {
            ownerVehicles.remove(vehicle);
            if (ownerVehicles.isEmpty()) {
                ownerMap.remove(vehicle.getOwnerName());
            }
        }

        // Check waitlist
        if (!waitlist.isEmpty()) {
            Vehicle nextVehicle = waitlist.poll();
            parkVehicle(nextVehicle);
        }

        return vehicle;
    }

    public List<Vehicle> searchByOwnerName(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty");
        }
        return ownerMap.getOrDefault(ownerName, new ArrayList<>());
    }

    public Vehicle searchByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty");
        }
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
        return new LinkedList<>(waitlist); // Return a copy to prevent external modification
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory); // Return a copy to prevent external modification
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