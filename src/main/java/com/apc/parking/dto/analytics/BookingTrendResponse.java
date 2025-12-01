package com.apc.parking.dto.analytics;

import java.util.List;

public class BookingTrendResponse {
    private List<DayBookingCount> dailyBookings;

    public BookingTrendResponse() {}

    public BookingTrendResponse(List<DayBookingCount> dailyBookings) {
        this.dailyBookings = dailyBookings;
    }

    public List<DayBookingCount> getDailyBookings() {
        return dailyBookings;
    }

    public void setDailyBookings(List<DayBookingCount> dailyBookings) {
        this.dailyBookings = dailyBookings;
    }

    public static class DayBookingCount {
        private String date; // YYYY-MM-DD
        private Long bookingCount;

        public DayBookingCount() {}

        public DayBookingCount(String date, Long bookingCount) {
            this.date = date;
            this.bookingCount = bookingCount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getBookingCount() {
            return bookingCount;
        }

        public void setBookingCount(Long bookingCount) {
            this.bookingCount = bookingCount;
        }
    }
}

