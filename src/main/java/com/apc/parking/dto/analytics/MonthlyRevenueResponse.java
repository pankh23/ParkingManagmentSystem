package com.apc.parking.dto.analytics;

import java.util.List;

public class MonthlyRevenueResponse {
    private List<MonthRevenue> monthlyRevenue;

    public MonthlyRevenueResponse() {}

    public MonthlyRevenueResponse(List<MonthRevenue> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<MonthRevenue> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<MonthRevenue> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public static class MonthRevenue {
        private String month; // e.g., "Jan", "Feb", "Mar"
        private String yearMonth; // e.g., "2024-01"
        private Double revenue;

        public MonthRevenue() {}

        public MonthRevenue(String month, String yearMonth, Double revenue) {
            this.month = month;
            this.yearMonth = yearMonth;
            this.revenue = revenue;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
        }

        public Double getRevenue() {
            return revenue;
        }

        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }
    }
}

