package com.apc.parking.controller;

import com.apc.parking.model.Vehicle;
import com.apc.parking.model.Transaction;
import com.apc.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:3000")
public class VehicleController {

    @Autowired
    private ParkingService parkingService;

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = parkingService.searchVehicle(id);
        if (vehicle != null) {
            return ResponseEntity.ok(vehicle);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody CreateVehicleRequest request) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(request.getId());
            vehicle.setType(request.getType());
            vehicle.setPlateNumber(request.getPlateNumber());
            vehicle.setParked(false);
            
            // Save vehicle (you might need to add this to ParkingService)
            return ResponseEntity.ok(vehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<Transaction>> getVehicleHistory(@PathVariable Long id) {
        try {
            List<Transaction> history = parkingService.getVehicleTransactionHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/available-slots/{type}")
    public ResponseEntity<Integer> getAvailableSlots(@PathVariable String type) {
        try {
            int availableSlots = parkingService.searchAvailableSlots(type).size();
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class CreateVehicleRequest {
        private Long id;
        private String type;
        private String plateNumber;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getPlateNumber() { return plateNumber; }
        public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    }
}
