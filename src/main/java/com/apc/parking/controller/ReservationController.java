package com.apc.parking.controller;

import com.apc.parking.model.Reservation;
import com.apc.parking.model.User;
import com.apc.parking.model.Vehicle;
import com.apc.parking.model.Slot;
import com.apc.parking.model.ParkingLot;
import com.apc.parking.service.ReservationService;
import com.apc.parking.service.SchedulerService;
import com.apc.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private SchedulerService schedulerService;
    
    @Autowired
    private ParkingLotService parkingLotService;

    // Static list to store demo reservations (since we're using localStorage in frontend)
    private static List<Reservation> demoReservations = new ArrayList<>();
    
    // Static map to store lot information for each reservation
    private static Map<Long, Map<String, Object>> reservationLotInfo = new java.util.HashMap<>();
    
    // Track slot availability for each parking lot
    private static Map<Long, Map<String, Integer>> slotAvailability = new java.util.HashMap<>();
    
    // Initialize slot availability for a parking lot
    private void initializeSlotAvailability(ParkingLot lot) {
        if (!slotAvailability.containsKey(lot.getId())) {
            Map<String, Integer> availability = new java.util.HashMap<>();
            availability.put("2W", lot.getTotalSlots2W());
            availability.put("4W", lot.getTotalSlots4W());
            slotAvailability.put(lot.getId(), availability);
        }
    }
    
    // Update slot availability when a slot is booked or released
    private void updateSlotAvailability(ParkingLot lot, String slotType, boolean isBooking) {
        // Recalculate availability based on current time
        recalculateSlotAvailability(lot);
    }
    
    // Recalculate slot availability based on currently active reservations
    private void recalculateSlotAvailability(ParkingLot lot) {
        initializeSlotAvailability(lot);
        Map<String, Integer> availability = slotAvailability.get(lot.getId());
        
        // Reset to total slots
        availability.put("2W", lot.getTotalSlots2W());
        availability.put("4W", lot.getTotalSlots4W());
        
        // Count currently active reservations for this parking lot
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (Reservation reservation : demoReservations) {
            if (reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED && 
                reservation.getSlot() != null) {
                
                // Check if this reservation belongs to the current parking lot
                boolean belongsToLot = false;
                
                // First check if the slot has a parking lot reference
                if (reservation.getSlot().getParkingLot() != null && 
                    reservation.getSlot().getParkingLot().getId().equals(lot.getId())) {
                    belongsToLot = true;
                } else {
                    // For demo purposes, check the lotId stored in reservationLotInfo
                    Map<String, Object> lotInfo = reservationLotInfo.get(reservation.getId());
                    if (lotInfo != null && lotInfo.get("lotId") != null) {
                        Long reservationLotId = ((Number) lotInfo.get("lotId")).longValue();
                        belongsToLot = reservationLotId.equals(lot.getId());
                    }
                }
                
                if (belongsToLot) {
                    // Check if reservation is currently active (started but not ended)
                    java.time.LocalDateTime startTime = reservation.getStartTime();
                    java.time.LocalDateTime endTime = reservation.getEndTime();
                    
                    if (now.isAfter(startTime) && now.isBefore(endTime)) {
                        // This reservation is currently active, reduce available slots
                        String slotType = reservation.getSlot().getType();
                        if (availability.containsKey(slotType)) {
                            int currentAvailable = availability.get(slotType);
                            availability.put(slotType, Math.max(0, currentAvailable - 1));
                        }
                    }
                }
            }
        }
    }
    
    // Get available slots for a parking lot
    public Map<String, Integer> getAvailableSlots(Long lotId) {
        ParkingLot lot = getParkingLotById(lotId);
        recalculateSlotAvailability(lot);
        return slotAvailability.get(lotId);
    }
    
    // Helper method to get parking lot information
    private ParkingLot getParkingLotById(Long lotId) {
        try {
            System.out.println("🔍 DEBUG: Looking for parking lot with ID: " + lotId);
            
            // For demo lots (1, 2, 3), prioritize mock data over database data
            if (lotId == 1L || lotId == 2L || lotId == 3L) {
                System.out.println("🔍 DEBUG: Using mock data for demo lot ID: " + lotId);
                return getMockParkingLotById(lotId);
            }
            
            // For other lots, try database first
            ParkingLot lot = parkingLotService.getParkingLotById(lotId);
            System.out.println("🔍 DEBUG: Database query result: " + (lot != null ? "FOUND" : "NULL"));
            if (lot != null) {
                System.out.println("Found lot in database: " + lot.getName() + " with " + lot.getTotalSlots2W() + " 2W and " + lot.getTotalSlots4W() + " 4W slots");
                return lot;
            }
            
            // If not found in database, check the hardcoded mock data
            System.out.println("🔍 DEBUG: Falling back to hardcoded mock data for lot ID: " + lotId);
            return getMockParkingLotById(lotId);

        } catch (Exception e) {
            System.err.println("Error getting parking lot by ID " + lotId + ": " + e.getMessage());
            // Fallback to mock data
            return getMockParkingLotById(lotId);
        }
    }
    
    // Helper method to get mock parking lot data (same as ParkingLotController)
    private ParkingLot getMockParkingLotById(Long lotId) {
        if (lotId == 1L) {
            ParkingLot lot = new ParkingLot();
            lot.setId(1L);
            lot.setName("Main Parking Lot");
            lot.setLocation("Building A - Ground Floor");
            lot.setTotalSlots2W(50);
            lot.setTotalSlots4W(30);
            lot.setPricePerHour2W(20.0);
            lot.setPricePerHour4W(40.0);
            lot.setIsActive(true);
            return lot;
        } else if (lotId == 2L) {
            ParkingLot lot = new ParkingLot();
            lot.setId(2L);
            lot.setName("Secondary Lot");
            lot.setLocation("Building B - Basement");
            lot.setTotalSlots2W(30);
            lot.setTotalSlots4W(20);
            lot.setPricePerHour2W(15.0);
            lot.setPricePerHour4W(35.0);
            lot.setIsActive(true);
            return lot;
        } else if (lotId == 3L) {
            ParkingLot lot = new ParkingLot();
            lot.setId(3L);
            lot.setName("VIP Parking");
            lot.setLocation("Building C - Level 1");
            lot.setTotalSlots2W(10);
            lot.setTotalSlots4W(15);
            lot.setPricePerHour2W(50.0);
            lot.setPricePerHour4W(80.0);
            lot.setIsActive(true);
            return lot;
        } else {
            // Default values for unknown lots
            ParkingLot lot = new ParkingLot();
            lot.setId(lotId);
            lot.setName("Parking Lot " + lotId);
            lot.setLocation("Main Location");
            lot.setTotalSlots2W(10);
            lot.setTotalSlots4W(5);
            lot.setPricePerHour2W(10.0);
            lot.setPricePerHour4W(20.0);
            lot.setIsActive(true);
            return lot;
        }
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody CreateReservationRequest request) {
        try {
            // Check slot availability before creating reservation
            ParkingLot lot = getParkingLotById(request.getLotId());
            String slotType = request.getSlotType() != null ? request.getSlotType() : "2W";
            
            // Get current availability
            Map<String, Integer> availability = getAvailableSlots(request.getLotId());
            int availableSlots = availability.getOrDefault(slotType, 0);
            
            if (availableSlots <= 0) {
                return ResponseEntity.badRequest().body("No available " + slotType + " slots in this parking lot. All slots are currently full.");
            }
            
            // For demo purposes, create a mock reservation with proper relationships
            Reservation reservation = new Reservation();
            reservation.setId(System.currentTimeMillis()); // Use timestamp as ID
            
            // Convert ISO string to LocalDateTime (handles timezone properly)
            java.time.LocalDateTime startTime = request.getStartTimeAsLocalDateTime();
            java.time.LocalDateTime endTime = request.getEndTimeAsLocalDateTime();
            
            // Timezone conversion completed successfully
            
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setTotalAmount(request.getTotalAmount());
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            
            // Create mock User
            User mockUser = new User();
            mockUser.setId(request.getUserId() != null ? request.getUserId() : 1L);
            mockUser.setUsername("demo-user-" + mockUser.getId());
            mockUser.setRole("USER");
            reservation.setUser(mockUser);
            
            // Create mock Vehicle
            Vehicle mockVehicle = new Vehicle();
            mockVehicle.setId(request.getVehicleId() != null ? request.getVehicleId() : 1L);
            // Use the vehicle number from request if provided, otherwise use demo number
            String vehicleNumber = request.getVehicleNumber() != null ? request.getVehicleNumber() : "DEMO-" + String.format("%03d", mockVehicle.getId());
            mockVehicle.setPlateNumber(vehicleNumber);
            mockVehicle.setType(request.getSlotType() != null ? request.getSlotType() : "2W");
            mockVehicle.setParked(true);
            reservation.setVehicle(mockVehicle);
            
            // Create mock Slot
            Slot mockSlot = new Slot();
            mockSlot.setId(System.currentTimeMillis() % 1000); // Generate slot ID
            mockSlot.setSlotNumber("S" + String.format("%03d", mockSlot.getId()));
            mockSlot.setType(request.getSlotType() != null ? request.getSlotType() : "2W");
            mockSlot.setOccupied(true);
            reservation.setSlot(mockSlot);
            
            // Update slot availability
            updateSlotAvailability(lot, request.getSlotType() != null ? request.getSlotType() : "2W", true);
            
            // Add lotId to the reservation for frontend display
            // We'll add this as a custom field for demo purposes
            Map<String, Object> reservationData = new java.util.HashMap<>();
            reservationData.put("id", reservation.getId());
            reservationData.put("startTime", reservation.getStartTime());
            reservationData.put("endTime", reservation.getEndTime());
            reservationData.put("totalAmount", reservation.getTotalAmount());
            reservationData.put("status", reservation.getStatus());
            reservationData.put("user", reservation.getUser());
            reservationData.put("vehicle", reservation.getVehicle());
            reservationData.put("slot", reservation.getSlot());
            // Get actual parking lot information (lot already defined above)
            reservationData.put("lotId", lot.getId());
            reservationData.put("lotName", lot.getName());
            
            // Store lot information for this reservation
            Map<String, Object> lotInfo = new java.util.HashMap<>();
            lotInfo.put("lotId", lot.getId());
            lotInfo.put("lotName", lot.getName());
            lotInfo.put("lotLocation", lot.getLocation());
            reservationLotInfo.put(reservation.getId(), lotInfo);
            
            // Add to demo list
            demoReservations.add(reservation);
            
            System.out.println("✅ Created demo reservation: " + reservation);
            System.out.println("📊 Total reservations now: " + demoReservations.size());
            return ResponseEntity.ok(reservationData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllReservations() {
        try {
            // Convert demo reservations to enhanced data format
            List<Map<String, Object>> enhancedReservations = new ArrayList<>();
            for (Reservation reservation : demoReservations) {
                Map<String, Object> reservationData = new java.util.HashMap<>();
                reservationData.put("id", reservation.getId());
                reservationData.put("startTime", reservation.getStartTime());
                reservationData.put("endTime", reservation.getEndTime());
                reservationData.put("totalAmount", reservation.getTotalAmount());
                reservationData.put("status", reservation.getStatus());
                reservationData.put("user", reservation.getUser());
                reservationData.put("vehicle", reservation.getVehicle());
                reservationData.put("slot", reservation.getSlot());
                // Get lot information for this reservation
                Map<String, Object> lotInfo = reservationLotInfo.get(reservation.getId());
                if (lotInfo != null) {
                    reservationData.put("lotId", lotInfo.get("lotId"));
                    reservationData.put("lotName", lotInfo.get("lotName"));
                } else {
                    // Fallback to default values
                    reservationData.put("lotId", 1L);
                    reservationData.put("lotName", "Main Parking Lot");
                }
                enhancedReservations.add(reservationData);
            }
            return ResponseEntity.ok(enhancedReservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<Map<String, Object>>> getReservationsByLot(@PathVariable Long lotId) {
        try {
            // For demo purposes, return all reservations
            // In a real app, you'd filter by lot ID through the slot relationship
            return getAllReservations();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/slots/availability/{lotId}")
    public ResponseEntity<Map<String, Integer>> getSlotAvailability(@PathVariable Long lotId) {
        Map<String, Integer> availability = getAvailableSlots(lotId);
        return ResponseEntity.ok(availability);
    }
    
    @GetMapping("/debug/lot/{lotId}")
    public ResponseEntity<Map<String, Object>> debugLot(@PathVariable Long lotId) {
        Map<String, Object> debug = new java.util.HashMap<>();
        debug.put("lotId", lotId);
        
        try {
            ParkingLot lot = parkingLotService.getParkingLotById(lotId);
            debug.put("serviceResult", lot != null ? "FOUND" : "NULL");
            if (lot != null) {
                debug.put("lotName", lot.getName());
                debug.put("totalSlots2W", lot.getTotalSlots2W());
                debug.put("totalSlots4W", lot.getTotalSlots4W());
                debug.put("location", lot.getLocation());
                debug.put("isActive", lot.getIsActive());
            }
        } catch (Exception e) {
            debug.put("serviceError", e.getMessage());
        }
        
        try {
            ParkingLot lot2 = getParkingLotById(lotId);
            debug.put("controllerResult", lot2 != null ? "FOUND" : "NULL");
            if (lot2 != null) {
                debug.put("controllerLotName", lot2.getName());
                debug.put("controllerTotalSlots2W", lot2.getTotalSlots2W());
                debug.put("controllerTotalSlots4W", lot2.getTotalSlots4W());
                debug.put("controllerLocation", lot2.getLocation());
                debug.put("controllerIsActive", lot2.getIsActive());
            }
        } catch (Exception e) {
            debug.put("controllerError", e.getMessage());
        }
        
        return ResponseEntity.ok(debug);
    }
    
    // Recalculate slot availability every minute
    @Scheduled(fixedRate = 60000) // 1 minute in milliseconds
    public void recalculateAllSlotAvailability() {
        try {
            // Recalculate for all known parking lots
            for (Long lotId : slotAvailability.keySet()) {
                ParkingLot lot = getParkingLotById(lotId);
                recalculateSlotAvailability(lot);
            }
        } catch (Exception e) {
            System.err.println("Error recalculating slot availability: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserReservations(@PathVariable Long userId) {
        try {
            // For demo purposes, return all reservations
            // In a real app, you'd filter by user ID
            return getAllReservations();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        try {
            Reservation reservation = demoReservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (reservation == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Long id, @RequestBody ConfirmReservationRequest request) {
        try {
            Reservation reservation = demoReservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (reservation == null) {
                return ResponseEntity.notFound().build();
            }
            
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            reservation.setTotalAmount(request.getTotalAmount());
            
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
        try {
            Reservation reservation = demoReservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (reservation == null) {
                return ResponseEntity.notFound().build();
            }
            
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/payment-link")
    public ResponseEntity<String> getPaymentLink(@PathVariable Long id) {
        try {
            // Mock payment link
            String paymentLink = "https://payment.example.com/pay/" + id;
            return ResponseEntity.ok(paymentLink);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Reservation>> getActiveReservations() {
        try {
            List<Reservation> activeReservations = demoReservations.stream()
                .filter(reservation -> reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .toList();
            
            return ResponseEntity.ok(activeReservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/process-completed")
    public ResponseEntity<String> processCompletedReservations() {
        try {
            System.out.println("🔄 Manually processing completed reservations...");
            
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            int processedCount = 0;
            
            for (Reservation reservation : demoReservations) {
                if (reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED && 
                    reservation.getEndTime().isBefore(now)) {
                    
                    // Mark as completed
                    reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                    
                    // Release the slot
                    if (reservation.getSlot() != null) {
                        reservation.getSlot().setOccupied(false);
                        reservation.getSlot().setVehicle(null);
                    }
                    
                    processedCount++;
                    System.out.println("✅ Completed reservation ID: " + reservation.getId() + 
                                     " (was: " + reservation.getStartTime() + " to " + reservation.getEndTime() + ")");
                }
            }
            
            if (processedCount > 0) {
                System.out.println("📊 Processed " + processedCount + " completed reservations");
                return ResponseEntity.ok("Processed " + processedCount + " completed reservations successfully");
            } else {
                System.out.println("ℹ️ No reservations found to mark as completed");
                return ResponseEntity.ok("No reservations found to mark as completed");
            }
        } catch (Exception e) {
            System.err.println("Error processing completed reservations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing completed reservations: " + e.getMessage());
        }
    }

    // DTOs
    public static class CreateReservationRequest {
        private Long userId;
        private Long vehicleId;
        private Long lotId;
        private String slotType;
        private String vehicleNumber;
        private String startTime; // Changed to String to handle ISO parsing
        private String endTime;   // Changed to String to handle ISO parsing
        private Double totalAmount;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public Long getLotId() { return lotId; }
        public void setLotId(Long lotId) { this.lotId = lotId; }
        public String getSlotType() { return slotType; }
        public void setSlotType(String slotType) { this.slotType = slotType; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        
        // Helper methods to convert to LocalDateTime
        public java.time.LocalDateTime getStartTimeAsLocalDateTime() {
            if (startTime == null) return null;
            try {
                // Try parsing as local timezone format first (YYYY-MM-DDTHH:mm:ss)
                if (startTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                    return java.time.LocalDateTime.parse(startTime);
                }
                // Fallback to ISO string parsing
                java.time.Instant instant = java.time.Instant.parse(startTime);
                return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            } catch (Exception e) {
                System.err.println("Error parsing startTime: " + startTime + " - " + e.getMessage());
                return java.time.LocalDateTime.now();
            }
        }
        
        public java.time.LocalDateTime getEndTimeAsLocalDateTime() {
            if (endTime == null) return null;
            try {
                // Try parsing as local timezone format first (YYYY-MM-DDTHH:mm:ss)
                if (endTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                    return java.time.LocalDateTime.parse(endTime);
                }
                // Fallback to ISO string parsing
                java.time.Instant instant = java.time.Instant.parse(endTime);
                return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            } catch (Exception e) {
                System.err.println("Error parsing endTime: " + endTime + " - " + e.getMessage());
                return java.time.LocalDateTime.now().plusHours(1);
            }
        }
    }

    public static class ConfirmReservationRequest {
        private Double totalAmount;

        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    }
}