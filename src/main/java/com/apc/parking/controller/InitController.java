package com.apc.parking.controller;

import com.apc.parking.model.*;
import com.apc.parking.service.ParkingLotService;
import com.apc.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private ParkingLotService parkingLotService;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private VehicleDao vehicleDao;
    
    @Autowired
    private SlotDao slotDao;
    
    @Autowired
    private TransactionTemplate transactionTemplate;

    @PostMapping("/data")
    public ResponseEntity<Map<String, Object>> initializeData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if data already exists
            if (!parkingLotService.getAllParkingLots().isEmpty()) {
                response.put("message", "Data already exists. Skipping initialization.");
                response.put("success", true);
                return ResponseEntity.ok(response);
            }
            
            transactionTemplate.execute(status -> {
                try {
                    // Create sample parking lots
                    ParkingLot lot1 = parkingLotService.createParkingLot(
                        "Main Parking Lot",
                        "Building A - Ground Floor",
                        50, 30, 20.0, 40.0
                    );
                    
                    ParkingLot lot2 = parkingLotService.createParkingLot(
                        "Secondary Lot",
                        "Building B - Basement",
                        30, 20, 15.0, 35.0
                    );
                    
                    ParkingLot lot3 = parkingLotService.createParkingLot(
                        "VIP Parking",
                        "Building C - Level 1",
                        10, 15, 50.0, 80.0
                    );
                    
                    // Create sample slots for each parking lot
                    createSlotsForLot(lot1, 50, 30);
                    createSlotsForLot(lot2, 30, 20);
                    createSlotsForLot(lot3, 10, 15);
                    
                    return null;
                } catch (Exception e) {
                    System.err.println("Error initializing data: " + e.getMessage());
                    e.printStackTrace();
                    status.setRollbackOnly();
                    throw new RuntimeException("Failed to initialize data: " + e.getMessage(), e);
                }
            });
            
            response.put("message", "Sample data initialized successfully!");
            response.put("success", true);
            response.put("parkingLotsCreated", 3);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "Failed to initialize data: " + e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private void createSlotsForLot(ParkingLot lot, int twoWheelerSlots, int fourWheelerSlots) {
        List<Slot> slots = new ArrayList<>();
        
        // Create 2W slots
        for (int i = 1; i <= twoWheelerSlots; i++) {
            Slot slot = new Slot();
            slot.setSlotNumber("2W-" + String.format("%03d", i));
            slot.setType("2W");
            slot.setOccupied(false);
            slot.setParkingLot(lot);
            slots.add(slot);
        }
        
        // Create 4W slots
        for (int i = 1; i <= fourWheelerSlots; i++) {
            Slot slot = new Slot();
            slot.setSlotNumber("4W-" + String.format("%03d", i));
            slot.setType("4W");
            slot.setOccupied(false);
            slot.setParkingLot(lot);
            slots.add(slot);
        }
        
        // Save all slots
        for (Slot slot : slots) {
            slotDao.save(slot);
        }
    }
}

