package com.apc.parking.service;

import com.apc.parking.model.ParkingLot;
import com.apc.parking.model.Slot;
import com.apc.parking.repository.ParkingLotDao;
import com.apc.parking.repository.SlotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotDao parkingLotDao;

    @Autowired
    private SlotDao slotDao;

    @Transactional
    public ParkingLot createParkingLot(String name, String location, Integer totalSlots2W, Integer totalSlots4W, 
                                     Double pricePerHour2W, Double pricePerHour4W) {
        System.out.println("ParkingLotService: Creating parking lot with parameters:");
        System.out.println("Name: " + name);
        System.out.println("Location: " + location);
        System.out.println("TotalSlots2W: " + totalSlots2W);
        System.out.println("TotalSlots4W: " + totalSlots4W);
        System.out.println("PricePerHour2W: " + pricePerHour2W);
        System.out.println("PricePerHour4W: " + pricePerHour4W);
        
        try {
            ParkingLot parkingLot = new ParkingLot();
            parkingLot.setName(name);
            parkingLot.setLocation(location);
            parkingLot.setTotalSlots2W(totalSlots2W);
            parkingLot.setTotalSlots4W(totalSlots4W);
            parkingLot.setPricePerHour2W(pricePerHour2W);
            parkingLot.setPricePerHour4W(pricePerHour4W);
            parkingLot.setIsActive(true);

            System.out.println("ParkingLotService: About to save parking lot to database");
            ParkingLot savedLot = parkingLotDao.save(parkingLot);
            System.out.println("ParkingLotService: Successfully saved parking lot with ID: " + savedLot.getId());
            
            // Create slots for the parking lot
            System.out.println("ParkingLotService: Creating slots for the parking lot");
            createSlotsForLot(savedLot);
            System.out.println("ParkingLotService: Successfully created all slots");
            
            return savedLot;
        } catch (Exception e) {
            System.err.println("ParkingLotService: Error creating parking lot: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create parking lot: " + e.getMessage(), e);
        }
    }

    @Transactional
    private void createSlotsForLot(ParkingLot parkingLot) {
        try {
            // Create 2W slots
            int total2W = parkingLot.getTotalSlots2W() != null ? parkingLot.getTotalSlots2W() : 0;
            for (int i = 1; i <= total2W; i++) {
                Slot slot = new Slot();
                slot.setSlotNumber("A" + String.format("%02d", i));
                slot.setType("2W");
                slot.setParkingLot(parkingLot);
                slot.setOccupied(false);
                slotDao.save(slot);
            }

            // Create 4W slots
            int total4W = parkingLot.getTotalSlots4W() != null ? parkingLot.getTotalSlots4W() : 0;
            for (int i = 1; i <= total4W; i++) {
                Slot slot = new Slot();
                slot.setSlotNumber("B" + String.format("%02d", i));
                slot.setType("4W");
                slot.setParkingLot(parkingLot);
                slot.setOccupied(false);
                slotDao.save(slot);
            }
        } catch (Exception e) {
            System.err.println("ParkingLotService: Error creating slots: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create slots for parking lot: " + e.getMessage(), e);
        }
    }

    public ParkingLot updateParkingLot(Long lotId, String name, String location, 
                                     Double pricePerHour2W, Double pricePerHour4W) {
        ParkingLot parkingLot = parkingLotDao.findById(lotId);
        if (parkingLot == null) {
            throw new RuntimeException("Parking lot not found with ID: " + lotId);
        }

        parkingLot.setName(name);
        parkingLot.setLocation(location);
        parkingLot.setPricePerHour2W(pricePerHour2W);
        parkingLot.setPricePerHour4W(pricePerHour4W);

        return parkingLotDao.update(parkingLot);
    }

    public void deleteParkingLot(Long lotId) {
        ParkingLot parkingLot = parkingLotDao.findById(lotId);
        if (parkingLot == null) {
            throw new RuntimeException("Parking lot not found with ID: " + lotId);
        }

        // Check if there are any active reservations
        // This would require checking reservations - implement as needed
        
        parkingLot.setIsActive(false);
        parkingLotDao.update(parkingLot);
    }

    @Transactional(readOnly = true)
    public ParkingLot getParkingLotById(Long lotId) {
        System.out.println("🔍 ParkingLotService: Looking for parking lot with ID: " + lotId);
        ParkingLot result = parkingLotDao.findById(lotId);
        System.out.println("🔍 ParkingLotService: Database query result: " + (result != null ? "FOUND - " + result.getName() : "NULL"));
        return result;
    }

    @Transactional(readOnly = true)
    public List<ParkingLot> getAllParkingLots() {
        try {
            System.out.println("ParkingLotService: Getting all parking lots from database...");
            List<ParkingLot> lots = parkingLotDao.findAll();
            System.out.println("ParkingLotService: Found " + (lots != null ? lots.size() : "null") + " parking lots");
            
            // Initialize lazy-loaded collections to avoid LazyInitializationException during JSON serialization
            if (lots != null && !lots.isEmpty()) {
                for (ParkingLot lot : lots) {
                    try {
                        // Access slots to force initialization while transaction is active
                        if (lot.getSlots() != null) {
                            lot.getSlots().size(); // Force initialization
                        }
                    } catch (Exception slotException) {
                        System.err.println("Warning: Could not initialize slots for lot " + lot.getId() + ": " + slotException.getMessage());
                        // Continue with other lots - don't fail the whole request
                    }
                }
            }
            return lots != null ? lots : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("ParkingLotService: Error getting all parking lots: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            e.printStackTrace();
            // Return empty list instead of throwing exception - let controller handle fallback
            System.err.println("ParkingLotService: Returning empty list due to error");
            return new java.util.ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ParkingLot> getActiveParkingLots() {
        try {
            List<ParkingLot> lots = parkingLotDao.findActiveLots();
            // Initialize lazy-loaded collections to avoid LazyInitializationException during JSON serialization
            if (lots != null) {
                for (ParkingLot lot : lots) {
                    if (lot.getSlots() != null) {
                        lot.getSlots().size(); // Force initialization
                    }
                }
            }
            return lots;
        } catch (Exception e) {
            System.err.println("ParkingLotService: Error getting active parking lots: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve active parking lots: " + e.getMessage(), e);
        }
    }

    public String getParkingLotStatus(Long lotId) {
        ParkingLot lot = parkingLotDao.findById(lotId);
        if (lot == null) {
            return "Parking lot not found";
        }

        int available2W = lot.getAvailableSlots2W();
        int available4W = lot.getAvailableSlots4W();
        int total2W = lot.getTotalSlots2W();
        int total4W = lot.getTotalSlots4W();

        return String.format("Parking Lot: %s\n" +
                "Location: %s\n" +
                "2W Slots: %d/%d available\n" +
                "4W Slots: %d/%d available\n" +
                "2W Price: ₹%.2f/hour\n" +
                "4W Price: ₹%.2f/hour\n" +
                "Status: %s",
                lot.getName(),
                lot.getLocation(),
                available2W, total2W,
                available4W, total4W,
                lot.getPricePerHour2W(),
                lot.getPricePerHour4W(),
                lot.getIsActive() ? "Active" : "Inactive");
    }
}
