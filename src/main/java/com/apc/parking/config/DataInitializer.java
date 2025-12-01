package com.apc.parking.config;

import com.apc.parking.model.*;
import com.apc.parking.service.ParkingLotService;
import com.apc.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data if database is empty
        try {
            if (parkingLotService.getAllParkingLots().isEmpty()) {
                System.out.println("Initializing sample data...");
                
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
                        
                        // Create sample users
                        User user1 = new User();
                        user1.setId(1L);
                        user1.setUsername("john_doe");
                        user1.setPassword("password123");
                        user1.setRole("USER");
                        userDao.save(user1);
                        
                        User admin = new User();
                        admin.setId(2L);
                        admin.setUsername("admin");
                        admin.setPassword("admin123");
                        admin.setRole("ADMIN");
                        userDao.save(admin);
                        
                        // Create sample vehicles
                        Vehicle vehicle1 = new Vehicle();
                        vehicle1.setId(1L);
                        vehicle1.setPlateNumber("ABC-123");
                        vehicle1.setType("2W");
                        vehicle1.setParked(false);
                        vehicle1.setUser(user1);
                        vehicleDao.save(vehicle1);
                        
                        Vehicle vehicle2 = new Vehicle();
                        vehicle2.setId(2L);
                        vehicle2.setPlateNumber("XYZ-789");
                        vehicle2.setType("4W");
                        vehicle2.setParked(false);
                        vehicle2.setUser(admin);
                        vehicleDao.save(vehicle2);
                        
                        // Create sample slots for each parking lot
                        createSlotsForLot(lot1, 50, 30);
                        createSlotsForLot(lot2, 30, 20);
                        createSlotsForLot(lot3, 10, 15);
                        
                        System.out.println("Sample data initialized successfully!");
                        return null;
                    } catch (Exception e) {
                        System.out.println("Error initializing sample data: " + e.getMessage());
                        status.setRollbackOnly();
                        return null;
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("Could not initialize sample data: " + e.getMessage());
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
