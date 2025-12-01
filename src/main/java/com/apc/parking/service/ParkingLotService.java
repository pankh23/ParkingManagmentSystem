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
    }

    @Transactional
    private void createSlotsForLot(ParkingLot parkingLot) {
        // Create 2W slots
        for (int i = 1; i <= parkingLot.getTotalSlots2W(); i++) {
            Slot slot = new Slot();
            slot.setSlotNumber("A" + String.format("%02d", i));
            slot.setType("2W");
            slot.setParkingLot(parkingLot);
            slot.setOccupied(false);
            slotDao.save(slot);
        }

        // Create 4W slots
        for (int i = 1; i <= parkingLot.getTotalSlots4W(); i++) {
            Slot slot = new Slot();
            slot.setSlotNumber("B" + String.format("%02d", i));
            slot.setType("4W");
            slot.setParkingLot(parkingLot);
            slot.setOccupied(false);
            slotDao.save(slot);
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

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotDao.findAll();
    }

    public List<ParkingLot> getActiveParkingLots() {
        return parkingLotDao.findActiveLots();
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
