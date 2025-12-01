package com.apc.parking.dto.analytics;

public class AnalyticsSummaryResponse {
    private Double totalRevenue;
    private Long totalBookings;
    private Long activeReservations;
    private Double averageOccupancy;
    private Double growthRatePercent;
    private Double todayRevenue;
    private Long todayBookings;

    public AnalyticsSummaryResponse() {}

    public AnalyticsSummaryResponse(Double totalRevenue, Long totalBookings, Long activeReservations,
                                   Double averageOccupancy, Double growthRatePercent, Double todayRevenue,
                                   Long todayBookings) {
        this.totalRevenue = totalRevenue;
        this.totalBookings = totalBookings;
        this.activeReservations = activeReservations;
        this.averageOccupancy = averageOccupancy;
        this.growthRatePercent = growthRatePercent;
        this.todayRevenue = todayRevenue;
        this.todayBookings = todayBookings;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Long getActiveReservations() {
        return activeReservations;
    }

    public void setActiveReservations(Long activeReservations) {
        this.activeReservations = activeReservations;
    }

    public Double getAverageOccupancy() {
        return averageOccupancy;
    }

    public void setAverageOccupancy(Double averageOccupancy) {
        this.averageOccupancy = averageOccupancy;
    }

    public Double getGrowthRatePercent() {
        return growthRatePercent;
    }

    public void setGrowthRatePercent(Double growthRatePercent) {
        this.growthRatePercent = growthRatePercent;
    }

    public Double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(Double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public Long getTodayBookings() {
        return todayBookings;
    }

    public void setTodayBookings(Long todayBookings) {
        this.todayBookings = todayBookings;
    }
}

