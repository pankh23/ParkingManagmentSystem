package com.apc.parking.service;

import com.apc.parking.dto.analytics.*;
import com.apc.parking.model.Payment;
import com.apc.parking.model.Reservation;
import com.apc.parking.model.ParkingLot;
import com.apc.parking.repository.PaymentDao;
import com.apc.parking.repository.ReservationDao;
import com.apc.parking.repository.ParkingLotDao;
import com.apc.parking.controller.ReservationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private ParkingLotDao parkingLotDao;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Get all reservations from both database and in-memory demo list
     */
    private List<Reservation> getAllReservations() {
        List<Reservation> allReservations = new ArrayList<>();
        
        // Get from database
        try {
            List<Reservation> dbReservations = reservationDao.findAll();
            if (dbReservations != null) {
                allReservations.addAll(dbReservations);
            }
        } catch (Exception e) {
            System.err.println("Error fetching reservations from database: " + e.getMessage());
        }
        
        // Get from in-memory demo list (from ReservationController)
        try {
            List<Reservation> demoReservations = ReservationController.getDemoReservations();
            if (demoReservations != null) {
                allReservations.addAll(demoReservations);
            }
        } catch (Exception e) {
            System.err.println("Error fetching demo reservations: " + e.getMessage());
        }
        
        return allReservations;
    }

    /**
     * Get peak hours analysis - bookings aggregated by hour of day
     */
    @Transactional(readOnly = true)
    public PeakHoursResponse getPeakHours() {
        try {
            List<Reservation> allReservations = getAllReservations();
            
            // Group by hour of startTime
            Map<Integer, Long> hourCountMap = allReservations.stream()
                    .filter(r -> r != null && r.getStartTime() != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .collect(Collectors.groupingBy(
                            r -> r.getStartTime().getHour(),
                            Collectors.counting()
                    ));

            // Convert to response format
            List<PeakHoursResponse.HourBookingCount> peakHours = new ArrayList<>();
            for (int hour = 0; hour < 24; hour++) {
                String hourStr = String.format("%02d:00", hour);
                Long count = hourCountMap.getOrDefault(hour, 0L);
                peakHours.add(new PeakHoursResponse.HourBookingCount(hourStr, count));
            }

            return new PeakHoursResponse(peakHours);
        } catch (Exception e) {
            System.err.println("Error in getPeakHours: " + e.getMessage());
            e.printStackTrace();
            // Return empty response on error
            List<PeakHoursResponse.HourBookingCount> peakHours = new ArrayList<>();
            for (int hour = 0; hour < 24; hour++) {
                peakHours.add(new PeakHoursResponse.HourBookingCount(String.format("%02d:00", hour), 0L));
            }
            return new PeakHoursResponse(peakHours);
        }
    }

    /**
     * Get vehicle type distribution
     */
    @Transactional(readOnly = true)
    public VehicleTypeDistributionResponse getVehicleTypeDistribution() {
        try {
            List<Reservation> allReservations = getAllReservations();
            
            long twoWheelerCount = allReservations.stream()
                    .filter(r -> r != null && r.getVehicle() != null && "2W".equals(r.getVehicle().getType()))
                    .count();
            
            long fourWheelerCount = allReservations.stream()
                    .filter(r -> r != null && r.getVehicle() != null && "4W".equals(r.getVehicle().getType()))
                    .count();

            return new VehicleTypeDistributionResponse(twoWheelerCount, fourWheelerCount);
        } catch (Exception e) {
            System.err.println("Error in getVehicleTypeDistribution: " + e.getMessage());
            e.printStackTrace();
            return new VehicleTypeDistributionResponse(0L, 0L);
        }
    }

    /**
     * Get daily occupancy for last 7 days per parking lot
     */
    @Transactional(readOnly = true)
    public DailyOccupancyResponse getDailyOccupancy() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6); // Last 7 days

            List<ParkingLot> allLots = parkingLotDao.findAll();
            if (allLots == null) {
                allLots = new ArrayList<>();
            }
            
            List<Reservation> allReservations = getAllReservations();
            
            List<DailyOccupancyResponse.LotOccupancyData> lotOccupancyDataList = new ArrayList<>();

            for (ParkingLot lot : allLots) {
                if (lot == null || lot.getId() == null) continue;
                
                List<DailyOccupancyResponse.DayOccupancy> dailyOccupancy = new ArrayList<>();
                
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    LocalDateTime dayStart = date.atStartOfDay();
                    LocalDateTime dayEnd = date.atTime(23, 59, 59);
                    
                    // Count occupied slots for this day
                    List<Reservation> dayReservations = allReservations.stream()
                            .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                            .filter(r -> {
                                if (r.getStartTime() == null || r.getEndTime() == null) return false;
                                LocalDateTime start = r.getStartTime();
                                LocalDateTime end = r.getEndTime();
                                return (start.isBefore(dayEnd) || start.isEqual(dayStart)) &&
                                       (end.isAfter(dayStart) || end.isEqual(dayEnd));
                            })
                            .filter(r -> r.getSlot() != null && 
                                       r.getSlot().getParkingLot() != null &&
                                       r.getSlot().getParkingLot().getId() != null &&
                                       r.getSlot().getParkingLot().getId().equals(lot.getId()))
                            .collect(Collectors.toList());

                    int occupiedSlots = (int) dayReservations.stream()
                            .filter(r -> r.getSlot() != null && r.getSlot().getId() != null)
                            .map(r -> r.getSlot().getId())
                            .distinct()
                            .count();

                    int totalSlots = (lot.getTotalSlots2W() != null ? lot.getTotalSlots2W() : 0) +
                                   (lot.getTotalSlots4W() != null ? lot.getTotalSlots4W() : 0);

                    dailyOccupancy.add(new DailyOccupancyResponse.DayOccupancy(
                            date.format(DATE_FORMATTER),
                            totalSlots,
                            occupiedSlots
                    ));
                }

                lotOccupancyDataList.add(new DailyOccupancyResponse.LotOccupancyData(
                        lot.getId(),
                        lot.getName() != null ? lot.getName() : "Unknown Lot",
                        dailyOccupancy
                ));
            }

            return new DailyOccupancyResponse(lotOccupancyDataList);
        } catch (Exception e) {
            System.err.println("Error in getDailyOccupancy: " + e.getMessage());
            e.printStackTrace();
            return new DailyOccupancyResponse(new ArrayList<>());
        }
    }

    /**
     * Get monthly revenue for last 12 months
     */
    @Transactional(readOnly = true)
    public MonthlyRevenueResponse getMonthlyRevenue() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1); // Last 12 months

            List<Payment> allPayments = paymentDao.findAll();
            if (allPayments == null) {
                allPayments = new ArrayList<>();
            }
            
            List<Reservation> allReservations = getAllReservations();
            
            // Filter payments in date range and group by month
            Map<String, Double> monthRevenueMap = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .filter(p -> {
                        LocalDateTime paidAt = p.getPaidAt();
                        if (paidAt == null) {
                            paidAt = p.getCreatedAt();
                        }
                        if (paidAt == null) return false;
                        LocalDate paymentDate = paidAt.toLocalDate();
                        return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                    })
                    .collect(Collectors.groupingBy(
                            p -> {
                                LocalDateTime paidAt = p.getPaidAt() != null ? p.getPaidAt() : p.getCreatedAt();
                                return paidAt != null ? paidAt.format(YEAR_MONTH_FORMATTER) : "";
                            },
                            Collectors.summingDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    ));
            
            // Also add revenue from reservations (grouped by creation month)
            Map<String, Double> reservationMonthRevenueMap = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getCreatedAt() != null && r.getTotalAmount() != null && r.getTotalAmount() > 0)
                    .filter(r -> {
                        LocalDate createdDate = r.getCreatedAt().toLocalDate();
                        return !createdDate.isBefore(startDate) && !createdDate.isAfter(endDate);
                    })
                    .collect(Collectors.groupingBy(
                            r -> r.getCreatedAt().format(YEAR_MONTH_FORMATTER),
                            Collectors.summingDouble(r -> r.getTotalAmount())
                    ));
            
            // Merge both maps (use max to avoid double counting)
            for (Map.Entry<String, Double> entry : reservationMonthRevenueMap.entrySet()) {
                String month = entry.getKey();
                Double reservationRevenue = entry.getValue();
                Double paymentRevenue = monthRevenueMap.getOrDefault(month, 0.0);
                monthRevenueMap.put(month, Math.max(paymentRevenue, reservationRevenue));
            }

            // Generate all months in range
            List<MonthlyRevenueResponse.MonthRevenue> monthlyRevenue = new ArrayList<>();
            for (int i = 11; i >= 0; i--) {
                LocalDate monthDate = endDate.minusMonths(i).withDayOfMonth(1);
                String yearMonth = monthDate.format(YEAR_MONTH_FORMATTER);
                String month = monthDate.format(MONTH_FORMATTER);
                Double revenue = monthRevenueMap.getOrDefault(yearMonth, 0.0);
                monthlyRevenue.add(new MonthlyRevenueResponse.MonthRevenue(month, yearMonth, revenue));
            }

            return new MonthlyRevenueResponse(monthlyRevenue);
        } catch (Exception e) {
            System.err.println("Error in getMonthlyRevenue: " + e.getMessage());
            e.printStackTrace();
            // Return empty response
            List<MonthlyRevenueResponse.MonthRevenue> monthlyRevenue = new ArrayList<>();
            LocalDate endDate = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                LocalDate monthDate = endDate.minusMonths(i).withDayOfMonth(1);
                monthlyRevenue.add(new MonthlyRevenueResponse.MonthRevenue(
                    monthDate.format(MONTH_FORMATTER),
                    monthDate.format(YEAR_MONTH_FORMATTER),
                    0.0
                ));
            }
            return new MonthlyRevenueResponse(monthlyRevenue);
        }
    }

    /**
     * Get top 5 parking lots by bookings/revenue
     */
    @Transactional(readOnly = true)
    public TopLotsResponse getTopLots() {
        try {
            List<ParkingLot> allLots = parkingLotDao.findAll();
            if (allLots == null) {
                allLots = new ArrayList<>();
            }
            
            List<Reservation> allReservations = getAllReservations();
            
            List<Payment> allPayments = paymentDao.findAll();
            if (allPayments == null) {
                allPayments = new ArrayList<>();
            }

            Map<Long, Long> lotBookingCountMap = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getSlot() != null && r.getSlot().getParkingLot() != null && r.getSlot().getParkingLot().getId() != null)
                    .collect(Collectors.groupingBy(
                            r -> r.getSlot().getParkingLot().getId(),
                            Collectors.counting()
                    ));

            Map<Long, Double> lotRevenueMap = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .filter(p -> p.getReservation() != null &&
                               p.getReservation().getSlot() != null &&
                               p.getReservation().getSlot().getParkingLot() != null &&
                               p.getReservation().getSlot().getParkingLot().getId() != null)
                    .collect(Collectors.groupingBy(
                            p -> p.getReservation().getSlot().getParkingLot().getId(),
                            Collectors.summingDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    ));
            
            // Also add revenue from reservations (if payments don't exist)
            Map<Long, Double> lotReservationRevenueMap = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getSlot() != null && r.getSlot().getParkingLot() != null && r.getSlot().getParkingLot().getId() != null)
                    .filter(r -> r.getTotalAmount() != null && r.getTotalAmount() > 0)
                    .collect(Collectors.groupingBy(
                            r -> r.getSlot().getParkingLot().getId(),
                            Collectors.summingDouble(r -> r.getTotalAmount())
                    ));
            
            // Merge both maps (use max to avoid double counting)
            for (Map.Entry<Long, Double> entry : lotReservationRevenueMap.entrySet()) {
                Long lotId = entry.getKey();
                Double reservationRevenue = entry.getValue();
                Double paymentRevenue = lotRevenueMap.getOrDefault(lotId, 0.0);
                lotRevenueMap.put(lotId, Math.max(paymentRevenue, reservationRevenue));
            }

            List<TopLotsResponse.TopLotData> topLots = allLots.stream()
                    .filter(lot -> lot != null && lot.getId() != null)
                    .map(lot -> {
                        Long bookings = lotBookingCountMap.getOrDefault(lot.getId(), 0L);
                        Double revenue = lotRevenueMap.getOrDefault(lot.getId(), 0.0);
                        return new TopLotsResponse.TopLotData(
                                lot.getId(),
                                lot.getName() != null ? lot.getName() : "Unknown Lot",
                                lot.getLocation() != null ? lot.getLocation() : "Unknown Location",
                                bookings,
                                revenue
                        );
                    })
                    .sorted((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue())) // Sort by revenue descending
                    .limit(5)
                    .collect(Collectors.toList());

            return new TopLotsResponse(topLots);
        } catch (Exception e) {
            System.err.println("Error in getTopLots: " + e.getMessage());
            e.printStackTrace();
            return new TopLotsResponse(new ArrayList<>());
        }
    }

    /**
     * Get analytics summary with key metrics
     */
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getAnalyticsSummary() {
        try {
            List<Payment> allPayments = paymentDao.findAll();
            if (allPayments == null) {
                allPayments = new ArrayList<>();
            }
            
            List<Reservation> allReservations = getAllReservations();
            
            List<ParkingLot> allLots = parkingLotDao.findAll();
            if (allLots == null) {
                allLots = new ArrayList<>();
            }

            // Total revenue from successful payments OR confirmed reservations (if no payment exists)
            Double totalRevenue = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .sum();
            
            // Also count revenue from confirmed reservations that don't have payments yet
            // This handles the case where reservations are created but payments aren't recorded
            Double reservationRevenue = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getTotalAmount() != null && r.getTotalAmount() > 0)
                    .mapToDouble(r -> r.getTotalAmount())
                    .sum();
            
            // Use the higher of the two (to avoid double counting if both exist)
            totalRevenue = Math.max(totalRevenue, reservationRevenue);

            // Total bookings
            Long totalBookings = (long) allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .count();

            // Active reservations (currently ongoing)
            LocalDateTime now = LocalDateTime.now();
            Long activeReservations = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> {
                        if (r.getStartTime() == null || r.getEndTime() == null) return false;
                        LocalDateTime start = r.getStartTime();
                        LocalDateTime end = r.getEndTime();
                        return (start.isBefore(now) || start.isEqual(now)) &&
                               (end.isAfter(now) || end.isEqual(now));
                    })
                    .count();

            // Average occupancy across all lots
            double totalOccupancy = 0.0;
            int lotsWithSlots = 0;
            for (ParkingLot lot : allLots) {
                if (lot == null || lot.getId() == null) continue;
                
                int totalSlots = (lot.getTotalSlots2W() != null ? lot.getTotalSlots2W() : 0) +
                               (lot.getTotalSlots4W() != null ? lot.getTotalSlots4W() : 0);
                if (totalSlots > 0) {
                    long occupiedSlots = allReservations.stream()
                            .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                            .filter(r -> {
                                if (r.getStartTime() == null || r.getEndTime() == null) return false;
                                LocalDateTime start = r.getStartTime();
                                LocalDateTime end = r.getEndTime();
                                return (start.isBefore(now) || start.isEqual(now)) &&
                                       (end.isAfter(now) || end.isEqual(now));
                            })
                            .filter(r -> r.getSlot() != null &&
                                       r.getSlot().getParkingLot() != null &&
                                       r.getSlot().getParkingLot().getId() != null &&
                                       r.getSlot().getParkingLot().getId().equals(lot.getId()))
                            .filter(r -> r.getSlot().getId() != null)
                            .map(r -> r.getSlot().getId())
                            .distinct()
                            .count();
                    totalOccupancy += (occupiedSlots * 100.0) / totalSlots;
                    lotsWithSlots++;
                }
            }
            Double averageOccupancy = lotsWithSlots > 0 ? totalOccupancy / lotsWithSlots : 0.0;

            // Today's revenue (from payments or reservations)
            LocalDate today = LocalDate.now();
            Double todayRevenue = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .filter(p -> {
                        LocalDateTime paidAt = p.getPaidAt() != null ? p.getPaidAt() : p.getCreatedAt();
                        return paidAt != null && paidAt.toLocalDate().equals(today);
                    })
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .sum();
            
            // Also count today's reservations
            Double todayReservationRevenue = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().toLocalDate().equals(today))
                    .filter(r -> r.getTotalAmount() != null && r.getTotalAmount() > 0)
                    .mapToDouble(r -> r.getTotalAmount())
                    .sum();
            
            todayRevenue = Math.max(todayRevenue, todayReservationRevenue);

            // Today's bookings
            Long todayBookings = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().toLocalDate().equals(today))
                    .count();

            // Growth rate calculation (compare this month with last month)
            LocalDate thisMonthStart = today.withDayOfMonth(1);
            LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDate lastMonthEnd = thisMonthStart.minusDays(1);

            Double thisMonthRevenue = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .filter(p -> {
                        LocalDateTime paidAt = p.getPaidAt() != null ? p.getPaidAt() : p.getCreatedAt();
                        if (paidAt == null) return false;
                        LocalDate paymentDate = paidAt.toLocalDate();
                        return !paymentDate.isBefore(thisMonthStart) && !paymentDate.isAfter(today);
                    })
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .sum();
            
            // Also count this month's reservations
            Double thisMonthReservationRevenue = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getCreatedAt() != null)
                    .filter(r -> {
                        LocalDate createdDate = r.getCreatedAt().toLocalDate();
                        return !createdDate.isBefore(thisMonthStart) && !createdDate.isAfter(today);
                    })
                    .filter(r -> r.getTotalAmount() != null && r.getTotalAmount() > 0)
                    .mapToDouble(r -> r.getTotalAmount())
                    .sum();
            
            thisMonthRevenue = Math.max(thisMonthRevenue, thisMonthReservationRevenue);

            Double lastMonthRevenue = allPayments.stream()
                    .filter(p -> p != null && p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                    .filter(p -> {
                        LocalDateTime paidAt = p.getPaidAt() != null ? p.getPaidAt() : p.getCreatedAt();
                        if (paidAt == null) return false;
                        LocalDate paymentDate = paidAt.toLocalDate();
                        return !paymentDate.isBefore(lastMonthStart) && !paymentDate.isAfter(lastMonthEnd);
                    })
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .sum();

            Double growthRatePercent = 0.0;
            if (lastMonthRevenue > 0) {
                growthRatePercent = ((thisMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100.0;
            } else if (thisMonthRevenue > 0) {
                growthRatePercent = 100.0; // 100% growth if no previous revenue
            }

            return new AnalyticsSummaryResponse(
                    totalRevenue,
                    totalBookings,
                    activeReservations,
                    averageOccupancy,
                    growthRatePercent,
                    todayRevenue,
                    todayBookings
            );
        } catch (Exception e) {
            System.err.println("Error in getAnalyticsSummary: " + e.getMessage());
            e.printStackTrace();
            return new AnalyticsSummaryResponse(0.0, 0L, 0L, 0.0, 0.0, 0.0, 0L);
        }
    }

    /**
     * Get booking trend for last 30 days
     */
    @Transactional(readOnly = true)
    public BookingTrendResponse getBookingTrend() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29); // Last 30 days

            List<Reservation> allReservations = getAllReservations();

            // Group by date
            Map<String, Long> dateBookingMap = allReservations.stream()
                    .filter(r -> r != null && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .filter(r -> r.getCreatedAt() != null)
                    .filter(r -> {
                        LocalDate createdDate = r.getCreatedAt().toLocalDate();
                        return !createdDate.isBefore(startDate) && !createdDate.isAfter(endDate);
                    })
                    .collect(Collectors.groupingBy(
                            r -> r.getCreatedAt().toLocalDate().format(DATE_FORMATTER),
                            Collectors.counting()
                    ));

            // Generate all dates in range
            List<BookingTrendResponse.DayBookingCount> dailyBookings = new ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String dateStr = date.format(DATE_FORMATTER);
                Long count = dateBookingMap.getOrDefault(dateStr, 0L);
                dailyBookings.add(new BookingTrendResponse.DayBookingCount(dateStr, count));
            }

            return new BookingTrendResponse(dailyBookings);
        } catch (Exception e) {
            System.err.println("Error in getBookingTrend: " + e.getMessage());
            e.printStackTrace();
            // Return empty response
            List<BookingTrendResponse.DayBookingCount> dailyBookings = new ArrayList<>();
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                dailyBookings.add(new BookingTrendResponse.DayBookingCount(date.format(DATE_FORMATTER), 0L));
            }
            return new BookingTrendResponse(dailyBookings);
        }
    }
}

