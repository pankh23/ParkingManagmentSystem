package com.apc.parking.dto.analytics;

import java.util.List;

public class DailyOccupancyResponse {
    private List<LotOccupancyData> lotOccupancyData;

    public DailyOccupancyResponse() {}

    public DailyOccupancyResponse(List<LotOccupancyData> lotOccupancyData) {
        this.lotOccupancyData = lotOccupancyData;
    }

    public List<LotOccupancyData> getLotOccupancyData() {
        return lotOccupancyData;
    }

    public void setLotOccupancyData(List<LotOccupancyData> lotOccupancyData) {
        this.lotOccupancyData = lotOccupancyData;
    }

    public static class LotOccupancyData {
        private Long lotId;
        private String lotName;
        private List<DayOccupancy> dailyOccupancy;

        public LotOccupancyData() {}

        public LotOccupancyData(Long lotId, String lotName, List<DayOccupancy> dailyOccupancy) {
            this.lotId = lotId;
            this.lotName = lotName;
            this.dailyOccupancy = dailyOccupancy;
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

        public List<DayOccupancy> getDailyOccupancy() {
            return dailyOccupancy;
        }

        public void setDailyOccupancy(List<DayOccupancy> dailyOccupancy) {
            this.dailyOccupancy = dailyOccupancy;
        }
    }

    public static class DayOccupancy {
        private String date; // YYYY-MM-DD
        private Integer totalSlots;
        private Integer occupiedSlots;
        private Double occupancyPercentage;

        public DayOccupancy() {}

        public DayOccupancy(String date, Integer totalSlots, Integer occupiedSlots) {
            this.date = date;
            this.totalSlots = totalSlots;
            this.occupiedSlots = occupiedSlots;
            if (totalSlots > 0) {
                this.occupancyPercentage = (occupiedSlots * 100.0) / totalSlots;
            } else {
                this.occupancyPercentage = 0.0;
            }
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getTotalSlots() {
            return totalSlots;
        }

        public void setTotalSlots(Integer totalSlots) {
            this.totalSlots = totalSlots;
        }

        public Integer getOccupiedSlots() {
            return occupiedSlots;
        }

        public void setOccupiedSlots(Integer occupiedSlots) {
            this.occupiedSlots = occupiedSlots;
        }

        public Double getOccupancyPercentage() {
            return occupancyPercentage;
        }

        public void setOccupancyPercentage(Double occupancyPercentage) {
            this.occupancyPercentage = occupancyPercentage;
        }
    }
}

