package com.apc.parking.controller;

import com.apc.parking.dto.analytics.*;
import com.apc.parking.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * GET /api/analytics/peak-hours
     * Returns hour → booking count for peak hours analysis
     */
    @GetMapping("/peak-hours")
    public ResponseEntity<PeakHoursResponse> getPeakHours() {
        try {
            PeakHoursResponse response = analyticsService.getPeakHours();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting peak hours: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/vehicle-type-distribution
     * Returns { twoWheelerCount, fourWheelerCount }
     */
    @GetMapping("/vehicle-type-distribution")
    public ResponseEntity<VehicleTypeDistributionResponse> getVehicleTypeDistribution() {
        try {
            VehicleTypeDistributionResponse response = analyticsService.getVehicleTypeDistribution();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting vehicle type distribution: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/daily-occupancy
     * Returns occupancy per lot/day for last 7 days
     */
    @GetMapping("/daily-occupancy")
    public ResponseEntity<DailyOccupancyResponse> getDailyOccupancy() {
        try {
            DailyOccupancyResponse response = analyticsService.getDailyOccupancy();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting daily occupancy: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/monthly-revenue
     * Returns total revenue per month (last 12 months)
     */
    @GetMapping("/monthly-revenue")
    public ResponseEntity<MonthlyRevenueResponse> getMonthlyRevenue() {
        try {
            MonthlyRevenueResponse response = analyticsService.getMonthlyRevenue();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting monthly revenue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/top-lots
     * Returns top 5 lots based on bookings or revenue
     */
    @GetMapping("/top-lots")
    public ResponseEntity<TopLotsResponse> getTopLots() {
        try {
            TopLotsResponse response = analyticsService.getTopLots();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting top lots: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/summary
     * Returns comprehensive analytics summary
     */
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getAnalyticsSummary() {
        try {
            AnalyticsSummaryResponse response = analyticsService.getAnalyticsSummary();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting analytics summary: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/booking-trend
     * Returns bookings count per day for last 30 days
     */
    @GetMapping("/booking-trend")
    public ResponseEntity<BookingTrendResponse> getBookingTrend() {
        try {
            BookingTrendResponse response = analyticsService.getBookingTrend();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting booking trend: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

