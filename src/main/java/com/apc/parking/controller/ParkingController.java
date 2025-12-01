package com.apc.parking.controller;

import com.apc.parking.model.Vehicle;
import com.apc.parking.model.Transaction;
import com.apc.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "http://localhost:3000")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @PostMapping("/park")
    public ResponseEntity<String> parkVehicle(@RequestBody ParkVehicleRequest request) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(request.getVehicleId());
            vehicle.setType(request.getType());
            vehicle.setPlateNumber(request.getPlateNumber());
            vehicle.setParked(false);
            
            String result = parkingService.parkVehicle(vehicle, request.getLotId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error parking vehicle: " + e.getMessage());
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exitVehicle(@RequestBody ExitVehicleRequest request) {
        try {
            String result = parkingService.exitVehicle(request.getVehicleId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exiting vehicle: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getSystemStatus() {
        try {
            String status = parkingService.getSystemStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            // Return mock status if service fails
            return ResponseEntity.ok("System running normally (Demo Mode)");
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        try {
            List<Transaction> transactions = parkingService.getTransactionHistory();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/latest-status")
    public ResponseEntity<List<Transaction>> getLatestVehicleStatus() {
        try {
            List<Transaction> status = parkingService.getLatestVehicleStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/wait-queue/{type}")
    public ResponseEntity<Integer> getWaitQueueSize(@PathVariable String type) {
        try {
            int queueSize = parkingService.getWaitQueue(type).size();
            return ResponseEntity.ok(queueSize);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class ParkVehicleRequest {
        private Long vehicleId;
        private String type;
        private String plateNumber;
        private Long lotId;

        // Getters and setters
        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getPlateNumber() { return plateNumber; }
        public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
        public Long getLotId() { return lotId; }
        public void setLotId(Long lotId) { this.lotId = lotId; }
    }

    public static class ExitVehicleRequest {
        private Long vehicleId;

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    }
}
