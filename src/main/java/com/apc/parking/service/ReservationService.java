package com.apc.parking.service;

import com.apc.parking.model.*;
import com.apc.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private SlotDao slotDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService;

    public Reservation createReservation(Long userId, Long vehicleId, Long lotId, String slotType, 
                                       LocalDateTime startTime, LocalDateTime endTime) {
        
        // Validate inputs
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Start time cannot be in the past");
        }
        
        if (endTime.isBefore(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        // Get user and vehicle
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Vehicle vehicle = vehicleDao.findById(vehicleId);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found");
        }

        // Find available slot
        Slot availableSlot = findAvailableSlot(lotId, slotType, startTime, endTime);
        if (availableSlot == null) {
            throw new RuntimeException("No available slots for the requested time period");
        }

        // Calculate total amount
        double totalAmount = calculateReservationAmount(availableSlot, startTime, endTime);

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setSlot(availableSlot);
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Reservation savedReservation = reservationDao.save(reservation);

        // Send notification
        notificationService.sendReservationCreatedNotification(savedReservation);

        return savedReservation;
    }

    private Slot findAvailableSlot(Long lotId, String slotType, LocalDateTime startTime, LocalDateTime endTime) {
        List<Slot> slots = slotDao.findAll();
        
        return slots.stream()
                .filter(slot -> slot.getParkingLot() != null && 
                               slot.getParkingLot().getId().equals(lotId) &&
                               slot.getType().equals(slotType) &&
                               !slot.isOccupied())
                .filter(slot -> !hasConflictingReservation(slot, startTime, endTime))
                .findFirst()
                .orElse(null);
    }

    private boolean hasConflictingReservation(Slot slot, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> slotReservations = reservationDao.findAll().stream()
                .filter(reservation -> reservation.getSlot().getId().equals(slot.getId()) &&
                                     reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .collect(java.util.stream.Collectors.toList());

        return slotReservations.stream()
                .anyMatch(reservation -> 
                    (startTime.isBefore(reservation.getEndTime()) && endTime.isAfter(reservation.getStartTime())));
    }

    private double calculateReservationAmount(Slot slot, LocalDateTime startTime, LocalDateTime endTime) {
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        if (hours == 0) hours = 1; // Minimum 1 hour

        ParkingLot lot = slot.getParkingLot();
        double hourlyRate = "2W".equals(slot.getType()) ? 
                lot.getPricePerHour2W() : lot.getPricePerHour4W();

        return hourlyRate * hours;
    }

    public Reservation confirmReservation(Long reservationId, String razorpayPaymentId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("Reservation not found");
        }

        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new RuntimeException("Reservation is not in pending status");
        }

        // Update reservation status
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationDao.update(reservation);

        // Update payment status
        paymentService.updatePaymentStatus(reservation, razorpayPaymentId, Payment.PaymentStatus.SUCCESSFUL);

        // Send confirmation notification
        notificationService.sendReservationConfirmedNotification(reservation);

        return reservation;
    }

    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("Reservation not found");
        }

        if (!reservation.canBeCancelled()) {
            throw new RuntimeException("Reservation cannot be cancelled");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationDao.update(reservation);

        // Process refund if payment was successful
        if (reservation.getTotalAmount() > 0) {
            paymentService.processRefund(reservation);
        }

        // Send cancellation notification
        notificationService.sendReservationCancelledNotification(reservation);

        return reservation;
    }

    public List<Reservation> getUserReservations(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return reservationDao.findByUser(user);
    }

    public List<Reservation> getAllReservations() {
        return reservationDao.findAll();
    }

    public List<Reservation> getActiveReservations() {
        return reservationDao.findActiveReservations();
    }

    public List<Reservation> getExpiredReservations() {
        return reservationDao.findExpiredReservations();
    }

    public void expireReservations() {
        List<Reservation> expiredReservations = getExpiredReservations();
        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
            reservationDao.update(reservation);
            
            // Send expiry notification
            notificationService.sendReservationExpiredNotification(reservation);
        }
    }

    public Reservation updateReservation(Reservation reservation) {
        return reservationDao.update(reservation);
    }

    public String getReservationStatus(Long reservationId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            return "Reservation not found";
        }

        return String.format("Reservation ID: %d\n" +
                "Slot: %s\n" +
                "Vehicle: %s\n" +
                "Start Time: %s\n" +
                "End Time: %s\n" +
                "Status: %s\n" +
                "Amount: ₹%.2f",
                reservation.getId(),
                reservation.getSlot().getSlotNumber(),
                reservation.getVehicle().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getTotalAmount());
    }
}
