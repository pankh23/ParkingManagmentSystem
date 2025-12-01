package com.apc.parking.service;

import com.apc.parking.model.*;
import com.apc.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.HashMap;

@Service
@Transactional
public class ParkingService {

    @Autowired
    private SlotDao slotDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private WaitQueueDao waitQueueDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private ReservationService reservationService;

    private Queue<Vehicle> twoWQueue = new LinkedList<>();
    private Queue<Vehicle> fourWQueue = new LinkedList<>();

    // =================== ADMIN FUNCTIONS ===================
    public void initializeSlots(int twoWCount, int fourWCount) {
        // Check if slots exist in database
        List<Slot> existingSlots = slotDao.findAll();
        
        if (existingSlots.isEmpty()) {
            // First time - create initial slots
            System.out.println("Creating initial slots: " + twoWCount + " for 2W and " + fourWCount + " for 4W vehicles...");
            for (int i = 0; i < twoWCount; i++) {
                Slot slot = new Slot();
                slot.setType("2W");
                slot.setOccupied(false);
                slotDao.save(slot);
            }
            for (int i = 0; i < fourWCount; i++) {
                Slot slot = new Slot();
                slot.setType("4W");
                slot.setOccupied(false);
                slotDao.save(slot);
            }
            System.out.println("Successfully created " + (twoWCount + fourWCount) + " total slots.");
        } else {
            // Slots already exist - use them as they are
            int twoWSlots = (int) existingSlots.stream().filter(s -> s.getType() != null && s.getType().equals("2W")).count();
            int fourWSlots = (int) existingSlots.stream().filter(s -> s.getType() != null && s.getType().equals("4W")).count();
            int occupiedTwoW = (int) existingSlots.stream().filter(s -> s.getType() != null && s.getType().equals("2W") && s.isOccupied()).count();
            int occupiedFourW = (int) existingSlots.stream().filter(s -> s.getType() != null && s.getType().equals("4W") && s.isOccupied()).count();
            
            System.out.println("Using existing slots from database:");
            System.out.println("2W slots: " + occupiedTwoW + "/" + twoWSlots + " occupied");
            System.out.println("4W slots: " + occupiedFourW + "/" + fourWSlots + " occupied");
        }
    }

    // =================== PARK VEHICLE ===================
    public String parkVehicle(Vehicle vehicle) {
        return parkVehicle(vehicle, null);
    }

    public String parkVehicle(Vehicle vehicle, Long lotId) {
        // Step 1: Handle vehicle persistence properly
        Vehicle managedVehicle;

        if (vehicle.getId() != null) {
            // Check if vehicle exists in database
            Vehicle existingVehicle = vehicleDao.findById(vehicle.getId());
            if (existingVehicle != null) {
                // Vehicle exists, update it
                existingVehicle.setParked(true);
                existingVehicle.setType(vehicle.getType());
                existingVehicle.setPlateNumber(vehicle.getPlateNumber());
                existingVehicle.setUser(vehicle.getUser());
                managedVehicle = vehicleDao.update(existingVehicle);
            } else {
                // Vehicle ID is set but doesn't exist in DB - create new with the provided ID
                vehicle.setParked(true);
                managedVehicle = vehicleDao.save(vehicle);
            }
        } else {
            // New vehicle - save it
            vehicle.setParked(true);
            managedVehicle = vehicleDao.save(vehicle);
        }

        // Step 2: Check for existing reservation
        if (managedVehicle.getUser() != null) {
            List<Reservation> activeReservations = reservationService.getActiveReservations().stream()
                    .filter(r -> r.getVehicle().getId().equals(managedVehicle.getId()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!activeReservations.isEmpty()) {
                Reservation reservation = activeReservations.get(0);
                Slot reservedSlot = reservation.getSlot();
                
                if (!reservedSlot.isOccupied()) {
                    // Park in reserved slot
                    reservedSlot.setOccupied(true);
                    reservedSlot.setVehicle(managedVehicle);
                    slotDao.update(reservedSlot);
                    
                    // Update reservation status
                    reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                    // Note: You'd need to update this through ReservationService
                    
                    // Create transaction
                    Transaction tx = new Transaction();
                    tx.setVehicle(managedVehicle);
                    tx.setSlot(reservedSlot);
                    tx.setAction("PARKED");
                    tx.setTimestamp(System.currentTimeMillis());
                    transactionDao.save(tx);
                    
                    return "Vehicle parked at reserved slot: " + reservedSlot.getSlotNumber() + 
                           " in lot: " + reservedSlot.getParkingLot().getName();
                }
            }
        }

        // Step 3: Find an empty slot (fallback for non-reserved vehicles)
        List<Slot> slots = slotDao.findAll();
        
        Optional<Slot> emptySlot = slots.stream()
                .filter(s -> !s.isOccupied() && s.getType().equals(managedVehicle.getType()))
                .filter(s -> lotId == null || s.getParkingLot().getId().equals(lotId))
                .findFirst();

        if (emptySlot.isPresent()) {
            Slot slot = emptySlot.get();
            slot.setOccupied(true);
            slot.setVehicle(managedVehicle);
            slotDao.update(slot);

            // Step 4: Create transaction with managed vehicle
            Transaction tx = new Transaction();
            tx.setVehicle(managedVehicle);
            tx.setSlot(slot);
            tx.setAction("PARKED");
            tx.setTimestamp(System.currentTimeMillis());
            transactionDao.save(tx);

            return "Vehicle parked at slot: " + slot.getSlotNumber() + 
                   " in lot: " + slot.getParkingLot().getName();
        } else {
            // No slot, add to wait queue
            WaitQueue wq = new WaitQueue();
            wq.setId(System.currentTimeMillis());
            wq.setVehicle(managedVehicle);
            wq.setSlotType(managedVehicle.getType());
            wq.setTimestamp(System.currentTimeMillis());
            waitQueueDao.save(wq);

            if (managedVehicle.getType().equals("2W")) twoWQueue.add(managedVehicle);
            else fourWQueue.add(managedVehicle);

            return "No slots available. Vehicle added to wait queue.";
        }
    }
    // =================== EXIT VEHICLE ===================
    public String exitVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleDao.findById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found.";
        }
        if (!vehicle.isParked()) {
            return "Vehicle is not currently parked.";
        }

        List<Slot> slots = slotDao.findAll();
        Slot slot = slots.stream()
                .filter(s -> s.getVehicle() != null && s.getVehicle().getId().equals(vehicleId))
                .findFirst().orElse(null);

        if (slot == null) {
            return "Error: Slot not found for this vehicle.";
        }

        // Update slot to be free
        slot.setOccupied(false);
        slot.setVehicle(null);
        slotDao.update(slot);

        // Update vehicle status
        vehicle.setParked(false);
        vehicleDao.update(vehicle);

        // Create exit transaction
        Transaction tx = new Transaction();
        tx.setVehicle(vehicle);
        tx.setSlot(slot);
        tx.setAction("EXITED");
        tx.setTimestamp(System.currentTimeMillis());
        transactionDao.save(tx);

        // Park next vehicle from queue if exists
        if (vehicle.getType().equals("2W") && !twoWQueue.isEmpty()) {
            Vehicle nextVehicle = twoWQueue.poll();
            parkVehicle(nextVehicle);
        } else if (vehicle.getType().equals("4W") && !fourWQueue.isEmpty()) {
            Vehicle nextVehicle = fourWQueue.poll();
            parkVehicle(nextVehicle);
        }

        return "Vehicle " + vehicleId + " exited from slot ID: " + slot.getId();
    }

    // =================== GET WAIT QUEUE ===================
    public List<WaitQueue> getWaitQueue(String type) {
        List<WaitQueue> all = waitQueueDao.findAll();
        List<WaitQueue> filtered = new ArrayList<>();
        for (WaitQueue wq : all) {
            if (wq.getSlotType().equals(type)) filtered.add(wq);
        }
        return filtered;
    }

    // =================== TRANSACTION HISTORY ===================
    public List<Transaction> getTransactionHistory() {
        return transactionDao.findAll();
    }

    // =================== LATEST VEHICLE STATUS ===================
    public List<Transaction> getLatestVehicleStatus() {
        List<Transaction> allTransactions = transactionDao.findAll();
        Map<Long, Transaction> latestStatus = new HashMap<>();
        
        // Get the latest transaction for each vehicle
        for (Transaction tx : allTransactions) {
            Long vehicleId = tx.getVehicle().getId();
            if (!latestStatus.containsKey(vehicleId) || 
                tx.getTimestamp() > latestStatus.get(vehicleId).getTimestamp()) {
                latestStatus.put(vehicleId, tx);
            }
        }
        
        return new ArrayList<>(latestStatus.values());
    }

    // =================== SEARCH VEHICLE ===================
    public Vehicle searchVehicle(Long vehicleId) {
        return vehicleDao.findById(vehicleId);
    }

    // =================== SEARCH AVAILABLE SLOTS ===================
    public List<Slot> searchAvailableSlots(String type) {
        List<Slot> slots = slotDao.findAll();
        List<Slot> available = new ArrayList<>();
        for (Slot s : slots) {
            if (!s.isOccupied() && s.getType().equals(type)) available.add(s);
        }
        return available;
    }

    // =================== VEHICLE TRANSACTION HISTORY ===================
    public List<Transaction> getVehicleTransactionHistory(Long vehicleId) {
        List<Transaction> allTx = transactionDao.findAll();
        List<Transaction> vehicleTx = new ArrayList<>();
        for (Transaction tx : allTx) {
            if (tx.getVehicle().getId().equals(vehicleId)) {
                vehicleTx.add(tx);
            }
        }
        return vehicleTx;
    }

    // =================== USER TRANSACTION HISTORY ===================
    public List<Transaction> getUserTransactionHistory(Long userId) {
        List<Transaction> allTx = transactionDao.findAll();
        List<Transaction> userTx = new ArrayList<>();
        for (Transaction tx : allTx) {
            if (tx.getVehicle().getUser() != null && tx.getVehicle().getUser().getId().equals(userId)) {
                userTx.add(tx);
            }
        }
        return userTx;
    }

    // =================== USER MANAGEMENT ===================
    public User getOrCreateUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            // Create new user if doesn't exist
            user = new User();
            user.setId(userId);
            user.setUsername("User" + userId);
            user.setPassword(""); // Empty password for now
            user.setRole("USER");
            userDao.save(user);
        }
        return user;
    }

    // =================== APPLICATION STATE MANAGEMENT ===================
    public void initializeApplicationState() {
        // Clear in-memory queues on startup
        twoWQueue.clear();
        fourWQueue.clear();
        
        // Load existing wait queue from database
        List<WaitQueue> waitQueues = waitQueueDao.findAll();
        for (WaitQueue wq : waitQueues) {
            if (wq.getSlotType().equals("2W")) {
                twoWQueue.add(wq.getVehicle());
            } else if (wq.getSlotType().equals("4W")) {
                fourWQueue.add(wq.getVehicle());
            }
        }
    }

    public String getSystemStatus() {
        List<Slot> slots = slotDao.findAll();
        long totalSlots = slots.size();
        long occupiedSlots = slots.stream().mapToLong(s -> s.isOccupied() ? 1 : 0).sum();
        long freeSlots = totalSlots - occupiedSlots;
        
        long twoWSlots = slots.stream().mapToLong(s -> s.getType().equals("2W") ? 1 : 0).sum();
        long fourWSlots = slots.stream().mapToLong(s -> s.getType().equals("4W") ? 1 : 0).sum();
        
        return String.format("System Status:\n" +
                "Total Slots: %d (2W: %d, 4W: %d)\n" +
                "Occupied: %d, Free: %d\n" +
                "Wait Queue - 2W: %d, 4W: %d",
                totalSlots, twoWSlots, fourWSlots, occupiedSlots, freeSlots,
                twoWQueue.size(), fourWQueue.size());
    }
}
