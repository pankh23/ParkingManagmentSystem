package com.apc.parking.dto.analytics;

import java.util.List;

public class PeakHoursResponse {
    private List<HourBookingCount> peakHours;

    public PeakHoursResponse() {}

    public PeakHoursResponse(List<HourBookingCount> peakHours) {
        this.peakHours = peakHours;
    }

    public List<HourBookingCount> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(List<HourBookingCount> peakHours) {
        this.peakHours = peakHours;
    }

    public static class HourBookingCount {
        private String hour; // e.g., "08:00", "09:00"
        private Long bookingCount;

        public HourBookingCount() {}

        public HourBookingCount(String hour, Long bookingCount) {
            this.hour = hour;
            this.bookingCount = bookingCount;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public Long getBookingCount() {
            return bookingCount;
        }

        public void setBookingCount(Long bookingCount) {
            this.bookingCount = bookingCount;
        }
    }
}

