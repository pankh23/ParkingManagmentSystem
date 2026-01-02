package com.apc.parking.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "APC Parking Management System API");
        response.put("status", "running");
        response.put("version", "2.0.0");
        response.put("endpoints", Map.of(
            "health", "/api/health",
            "auth", "/api/auth",
            "parking-lots", "/api/parking-lots",
            "reservations", "/api/reservations",
            "vehicles", "/api/vehicles",
            "analytics", "/api/analytics"
        ));
        return response;
    }
}

