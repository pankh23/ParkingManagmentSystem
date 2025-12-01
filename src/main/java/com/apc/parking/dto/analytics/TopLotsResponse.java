package com.apc.parking.dto.analytics;

import java.util.List;

public class TopLotsResponse {
    private List<TopLotData> topLots;

    public TopLotsResponse() {}

    public TopLotsResponse(List<TopLotData> topLots) {
        this.topLots = topLots;
    }

    public List<TopLotData> getTopLots() {
        return topLots;
    }

    public void setTopLots(List<TopLotData> topLots) {
        this.topLots = topLots;
    }

    public static class TopLotData {
        private Long lotId;
        private String lotName;
        private String location;
        private Long totalBookings;
        private Double totalRevenue;
        private Double averageRevenuePerBooking;

        public TopLotData() {}

        public TopLotData(Long lotId, String lotName, String location, Long totalBookings, Double totalRevenue) {
            this.lotId = lotId;
            this.lotName = lotName;
            this.location = location;
            this.totalBookings = totalBookings;
            this.totalRevenue = totalRevenue;
            if (totalBookings > 0) {
                this.averageRevenuePerBooking = totalRevenue / totalBookings;
            } else {
                this.averageRevenuePerBooking = 0.0;
            }
        }

        public Long getLotId() {
            return lotId;
        }

        public void setLotId(Long lotId) {
            this.lotId = lotId;
        }

        public String getLotName() {
            return lotName;
        }

        public void setLotName(String lotName) {
            this.lotName = lotName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getTotalBookings() {
            return totalBookings;
        }

        public void setTotalBookings(Long totalBookings) {
            this.totalBookings = totalBookings;
        }

        public Double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(Double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public Double getAverageRevenuePerBooking() {
            return averageRevenuePerBooking;
        }

        public void setAverageRevenuePerBooking(Double averageRevenuePerBooking) {
            this.averageRevenuePerBooking = averageRevenuePerBooking;
        }
    }
}

