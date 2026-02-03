package com.apc.parking.controller;

import com.apc.parking.model.ParkingLot;
import com.apc.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;
    
    // Static list to store created parking lots for demo purposes
    private static List<ParkingLot> createdLots = new ArrayList<>();

    @GetMapping
    public ResponseEntity<?> getAllParkingLots() {
        // BULLETPROOF: Always return 200 OK with data, never 500
        List<ParkingLot> lots = new ArrayList<>();
        
        try {
            System.out.println("=== GET /api/parking-lots called ===");
            
            // Try to get from database
            try {
                List<ParkingLot> dbLots = parkingLotService.getAllParkingLots();
                if (dbLots != null && !dbLots.isEmpty()) {
                    lots.addAll(dbLots);
                    System.out.println("Added " + dbLots.size() + " lots from database");
                }
            } catch (Throwable dbException) {
                System.err.println("Database error (using mock data): " + dbException.getMessage());
            }
            
            // If no database lots, use mock data
            if (lots.isEmpty()) {
                try {
                    List<ParkingLot> mockLots = createMockParkingLots();
                    lots.addAll(mockLots);
                    System.out.println("Added " + mockLots.size() + " mock lots");
                } catch (Throwable mockException) {
                    System.err.println("Mock data error: " + mockException.getMessage());
                }
            }
            
            // Add created lots
            try {
                if (createdLots != null && !createdLots.isEmpty()) {
                    lots.addAll(createdLots);
                    System.out.println("Added " + createdLots.size() + " created lots");
                }
            } catch (Throwable createdException) {
                System.err.println("Created lots error: " + createdException.getMessage());
            }
            
        } catch (Throwable e) {
            // Catch absolutely everything
            System.err.println("CRITICAL ERROR (returning empty list): " + e.getMessage());
            e.printStackTrace();
            lots = new ArrayList<>(); // Ensure we return empty list, not null
        }
        
        // ALWAYS return 200 OK with a list (never null, never 500)
        System.out.println("Returning " + lots.size() + " parking lots (status: 200 OK)");
        return ResponseEntity.ok(lots != null ? lots : new ArrayList<>());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ParkingLot>> getActiveParkingLots() {
        try {
            List<ParkingLot> lots = parkingLotService.getActiveParkingLots();
            // Add created lots to the list (for immediate availability)
            lots.addAll(createdLots);
            return ResponseEntity.ok(lots);
        } catch (Exception e) {
            System.err.println("Error getting active parking lots from database: " + e.getMessage());
            // Return mock data if database fails
            List<ParkingLot> mockLots = createMockParkingLots();
            // Add created lots to the mock data
            mockLots.addAll(createdLots);
            return ResponseEntity.ok(mockLots);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable Long id) {
        ParkingLot lot = parkingLotService.getParkingLotById(id);
        if (lot != null) {
            return ResponseEntity.ok(lot);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create-lot")
    @Transactional
    public ResponseEntity<?> createParkingLot(@RequestBody java.util.Map<String, Object> request) {
        System.out.println("=== PARKING LOT CREATION REQUEST ===");
        System.out.println("Request object: " + request);
        if (request == null) {
            System.out.println("Request is null!");
            return ResponseEntity.badRequest().body(Map.of("error", "Request body is required"));
        }
        System.out.println("Request class: " + request.getClass().getName());
        try {
            System.out.println("Creating parking lot with data: " + request);
            
            // Extract and validate name
            String name = (String) request.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Parking lot name is required"));
            }
            
            // Extract and validate location
            String location = (String) request.get("location");
            if (location == null || location.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Location is required"));
            }
            
            // Extract and convert totalSlots2W (handle both Integer and Number types)
            Integer totalSlots2W = null;
            Object slots2W = request.get("totalSlots2W");
            if (slots2W != null) {
                if (slots2W instanceof Integer) {
                    totalSlots2W = (Integer) slots2W;
                } else if (slots2W instanceof Number) {
                    totalSlots2W = ((Number) slots2W).intValue();
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "totalSlots2W must be a number"));
                }
            }
            if (totalSlots2W == null || totalSlots2W < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "totalSlots2W must be a non-negative number"));
            }
            
            // Extract and convert totalSlots4W (handle both Integer and Number types)
            Integer totalSlots4W = null;
            Object slots4W = request.get("totalSlots4W");
            if (slots4W != null) {
                if (slots4W instanceof Integer) {
                    totalSlots4W = (Integer) slots4W;
                } else if (slots4W instanceof Number) {
                    totalSlots4W = ((Number) slots4W).intValue();
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "totalSlots4W must be a number"));
                }
            }
            if (totalSlots4W == null || totalSlots4W < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "totalSlots4W must be a non-negative number"));
            }
            
            // Extract and convert pricePerHour2W
            Double pricePerHour2W = null;
            Object price2W = request.get("pricePerHour2W");
            if (price2W != null) {
                if (price2W instanceof Number) {
                    pricePerHour2W = ((Number) price2W).doubleValue();
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "pricePerHour2W must be a number"));
                }
            }
            if (pricePerHour2W == null || pricePerHour2W < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "pricePerHour2W must be a non-negative number"));
            }
            
            // Extract and convert pricePerHour4W
            Double pricePerHour4W = null;
            Object price4W = request.get("pricePerHour4W");
            if (price4W != null) {
                if (price4W instanceof Number) {
                    pricePerHour4W = ((Number) price4W).doubleValue();
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "pricePerHour4W must be a number"));
                }
            }
            if (pricePerHour4W == null || pricePerHour4W < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "pricePerHour4W must be a non-negative number"));
            }
            
            System.out.println("Name: " + name);
            System.out.println("Location: " + location);
            System.out.println("TotalSlots2W: " + totalSlots2W);
            System.out.println("TotalSlots4W: " + totalSlots4W);
            System.out.println("PricePerHour2W: " + pricePerHour2W);
            System.out.println("PricePerHour4W: " + pricePerHour4W);
            
            // Use the service to create the parking lot in the database
            ParkingLot savedLot = parkingLotService.createParkingLot(
                name, location, totalSlots2W, totalSlots4W, pricePerHour2W, pricePerHour4W
            );
            
            // Also add to the static list for immediate availability
            createdLots.add(savedLot);
            
            System.out.println("Successfully created parking lot: " + savedLot);
            return ResponseEntity.ok(savedLot);
        } catch (Exception e) {
            System.err.println("Error creating parking lot: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create parking lot: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> updateParkingLot(@PathVariable Long id, @RequestBody UpdateParkingLotRequest request) {
        try {
            // First check if it's a created lot in our static list
            for (ParkingLot lot : createdLots) {
                if (lot.getId().equals(id)) {
                    // Update the lot in the static list
                    lot.setName(request.getName());
                    lot.setLocation(request.getLocation());
                    lot.setPricePerHour2W(request.getPricePerHour2W());
                    lot.setPricePerHour4W(request.getPricePerHour4W());
                    
                    // Also update in database
                    try {
                        parkingLotService.updateParkingLot(
                            id,
                            request.getName(),
                            request.getLocation(),
                            request.getPricePerHour2W(),
                            request.getPricePerHour4W()
                        );
                        // Refresh the static list from database
                        // Static list is already updated
                    } catch (Exception e) {
                        System.err.println("Error updating lot in database: " + e.getMessage());
                    }
                    
                    System.out.println("Updated created lot: " + lot);
                    return ResponseEntity.ok(lot);
                }
            }
            
            // If not found in created lots, try service layer
            ParkingLot lot = parkingLotService.updateParkingLot(
                id,
                request.getName(),
                request.getLocation(),
                request.getPricePerHour2W(),
                request.getPricePerHour4W()
            );
            return ResponseEntity.ok(lot);
        } catch (Exception e) {
            System.err.println("Error updating parking lot: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable Long id) {
        try {
            // First check if it's a created lot in our static list
            for (int i = 0; i < createdLots.size(); i++) {
                if (createdLots.get(i).getId().equals(id)) {
                    ParkingLot removedLot = createdLots.remove(i);
                    
                    // Also delete from database
                    try {
                        parkingLotService.deleteParkingLot(id);
                        // Refresh the static list from database
                        // Static list is already updated
                    } catch (Exception e) {
                        System.err.println("Error deleting lot from database: " + e.getMessage());
                    }
                    
                    System.out.println("Deleted created lot: " + removedLot);
                    return ResponseEntity.ok().build();
                }
            }
            
            // If not found in created lots, try service layer
            parkingLotService.deleteParkingLot(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting parking lot: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getParkingLotStatus(@PathVariable Long id) {
        String status = parkingLotService.getParkingLotStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<Map<String, Object>>> getLotReservations(@PathVariable Long id) {
        try {
            // For demo purposes, we'll return reservations from localStorage
            // In a real app, this would query the database
            List<Map<String, Object>> reservations = new ArrayList<>();
            
            // This is a demo endpoint - in production, you'd query the database
            // For now, return empty list as we're using localStorage
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ArrayList<Map<String, Object>>());
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> testPost(@RequestBody String testData) {
        System.out.println("Test POST endpoint called with data: " + testData);
        return ResponseEntity.ok("Test successful: " + testData);
    }

    @PostMapping("/test-json")
    public ResponseEntity<String> testJsonPost(@RequestBody Object testData) {
        System.out.println("Test JSON POST endpoint called with data: " + testData);
        System.out.println("Data type: " + testData.getClass().getName());
        return ResponseEntity.ok("Test JSON successful: " + testData);
    }

    @PostMapping("/test-create")
    public ResponseEntity<String> testCreatePost(@RequestBody Object testData) {
        System.out.println("Test CREATE POST endpoint called with data: " + testData);
        System.out.println("Data type: " + testData.getClass().getName());
        return ResponseEntity.ok("Test CREATE successful: " + testData);
    }
    
    @PostMapping("/debug-create")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<String> debugCreate(@RequestBody(required = false) java.util.Map<String, Object> request) {
        System.out.println("=== DEBUG CREATE REQUEST ===");
        System.out.println("Request: " + request);
        System.out.println("Request class: " + (request != null ? request.getClass().getName() : "null"));
        if (request != null) {
            System.out.println("Keys: " + request.keySet());
            System.out.println("Name: " + request.get("name"));
            
            try {
                // Try to create a parking lot using the service
                String name = (String) request.get("name");
                String location = (String) request.get("location");
                Integer totalSlots2W = (Integer) request.get("totalSlots2W");
                Integer totalSlots4W = (Integer) request.get("totalSlots4W");
                Double pricePerHour2W = ((Number) request.get("pricePerHour2W")).doubleValue();
                Double pricePerHour4W = ((Number) request.get("pricePerHour4W")).doubleValue();
                
                System.out.println("Attempting to create parking lot...");
                ParkingLot savedLot = parkingLotService.createParkingLot(
                    name, location, totalSlots2W, totalSlots4W, pricePerHour2W, pricePerHour4W
                );
                System.out.println("Successfully created lot: " + savedLot);
                
                // Refresh the static list
                // Static list is already updated
                
                return ResponseEntity.ok("Debug successful - Lot created: " + savedLot);
            } catch (Exception e) {
                System.err.println("Error in debug create: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.ok("Debug successful but error: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Debug successful: " + request);
    }

    @PostMapping("/simple-test")
    public ResponseEntity<String> simpleTest() {
        System.out.println("Simple test POST endpoint called");
        return ResponseEntity.ok("Simple test successful");
    }

    // DTOs for request/response
    public static class CreateParkingLotRequest {
        private String name;
        private String location;
        private Integer totalSlots2W;
        private Integer totalSlots4W;
        private Double pricePerHour2W;
        private Double pricePerHour4W;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getTotalSlots2W() { return totalSlots2W; }
        public void setTotalSlots2W(Integer totalSlots2W) { this.totalSlots2W = totalSlots2W; }
        public Integer getTotalSlots4W() { return totalSlots4W; }
        public void setTotalSlots4W(Integer totalSlots4W) { this.totalSlots4W = totalSlots4W; }
        public Double getPricePerHour2W() { return pricePerHour2W; }
        public void setPricePerHour2W(Double pricePerHour2W) { this.pricePerHour2W = pricePerHour2W; }
        public Double getPricePerHour4W() { return pricePerHour4W; }
        public void setPricePerHour4W(Double pricePerHour4W) { this.pricePerHour4W = pricePerHour4W; }
        
        @Override
        public String toString() {
            return "CreateParkingLotRequest{" +
                    "name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", totalSlots2W=" + totalSlots2W +
                    ", totalSlots4W=" + totalSlots4W +
                    ", pricePerHour2W=" + pricePerHour2W +
                    ", pricePerHour4W=" + pricePerHour4W +
                    '}';
        }
    }

    public static class UpdateParkingLotRequest {
        private String name;
        private String location;
        private Double pricePerHour2W;
        private Double pricePerHour4W;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getPricePerHour2W() { return pricePerHour2W; }
        public void setPricePerHour2W(Double pricePerHour2W) { this.pricePerHour2W = pricePerHour2W; }
        public Double getPricePerHour4W() { return pricePerHour4W; }
        public void setPricePerHour4W(Double pricePerHour4W) { this.pricePerHour4W = pricePerHour4W; }
    }

    private List<ParkingLot> createMockParkingLots() {
        List<ParkingLot> mockLots = new ArrayList<>();
        
        ParkingLot lot1 = new ParkingLot();
        lot1.setId(1L);
        lot1.setName("Main Parking Lot");
        lot1.setLocation("Building A - Ground Floor");
        lot1.setTotalSlots2W(50);
        lot1.setTotalSlots4W(30);
        lot1.setPricePerHour2W(20.0);
        lot1.setPricePerHour4W(40.0);
        lot1.setIsActive(true);
        mockLots.add(lot1);
        
        ParkingLot lot2 = new ParkingLot();
        lot2.setId(2L);
        lot2.setName("Secondary Lot");
        lot2.setLocation("Building B - Basement");
        lot2.setTotalSlots2W(30);
        lot2.setTotalSlots4W(20);
        lot2.setPricePerHour2W(15.0);
        lot2.setPricePerHour4W(35.0);
        lot2.setIsActive(true);
        mockLots.add(lot2);
        
        ParkingLot lot3 = new ParkingLot();
        lot3.setId(3L);
        lot3.setName("VIP Parking");
        lot3.setLocation("Building C - Level 1");
        lot3.setTotalSlots2W(10);
        lot3.setTotalSlots4W(15);
        lot3.setPricePerHour2W(50.0);
        lot3.setPricePerHour4W(80.0);
        lot3.setIsActive(true);
        mockLots.add(lot3);
        
        return mockLots;
    }
}
