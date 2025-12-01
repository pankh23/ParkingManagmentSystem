package com.apc.parking.service;

import com.apc.parking.model.Slot;
import com.apc.parking.model.Reservation;
import com.apc.parking.repository.SlotDao;
import com.apc.parking.repository.ReservationDao;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ConcurrencyService {

    @Autowired
    private SlotDao slotDao;

    @Autowired
    private ReservationDao reservationDao;

    // Locks for different slot types to prevent race conditions
    private final ReentrantLock twoWLock = new ReentrantLock();
    private final ReentrantLock fourWLock = new ReentrantLock();

    /**
     * Thread-safe slot assignment with optimistic locking
     */
    @Transactional
    public Slot assignSlotSafely(Long lotId, String slotType) {
        ReentrantLock lock = "2W".equals(slotType) ? twoWLock : fourWLock;
        
        lock.lock();
        try {
            return assignSlotWithRetry(lotId, slotType, 3);
        } finally {
            lock.unlock();
        }
    }

    private Slot assignSlotWithRetry(Long lotId, String slotType, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return findAndAssignSlot(lotId, slotType);
            } catch (StaleObjectStateException e) {
                if (attempt == maxRetries - 1) {
                    throw new RuntimeException("Failed to assign slot after " + maxRetries + " attempts due to concurrency conflicts");
                }
                // Wait briefly before retry
                try {
                    Thread.sleep(50 + (attempt * 25)); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during slot assignment retry");
                }
            }
        }
        return null;
    }

    private Slot findAndAssignSlot(Long lotId, String slotType) {
        List<Slot> slots = slotDao.findAll();
        
        Slot availableSlot = slots.stream()
                .filter(slot -> slot.getParkingLot() != null && 
                               slot.getParkingLot().getId().equals(lotId) &&
                               slot.getType().equals(slotType) &&
                               !slot.isOccupied())
                .findFirst()
                .orElse(null);

        if (availableSlot != null) {
            // Use optimistic locking - Hibernate will check version
            availableSlot.setOccupied(true);
            return slotDao.update(availableSlot);
        }
        
        return null;
    }

    /**
     * Thread-safe reservation creation with conflict checking
     */
    @Transactional
    public Reservation createReservationSafely(Reservation reservation) {
        ReentrantLock lock = "2W".equals(reservation.getSlot().getType()) ? twoWLock : fourWLock;
        
        lock.lock();
        try {
            return createReservationWithRetry(reservation, 3);
        } finally {
            lock.unlock();
        }
    }

    private Reservation createReservationWithRetry(Reservation reservation, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return createReservationInternal(reservation);
            } catch (StaleObjectStateException e) {
                if (attempt == maxRetries - 1) {
                    throw new RuntimeException("Failed to create reservation after " + maxRetries + " attempts due to concurrency conflicts");
                }
                // Wait briefly before retry
                try {
                    Thread.sleep(50 + (attempt * 25));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during reservation creation retry");
                }
            }
        }
        return null;
    }

    private Reservation createReservationInternal(Reservation reservation) {
        // Check for conflicts with existing reservations
        if (hasConflictingReservation(reservation)) {
            throw new RuntimeException("Slot is not available for the requested time period");
        }

        // Create the reservation
        return reservationDao.save(reservation);
    }

    private boolean hasConflictingReservation(Reservation newReservation) {
        List<Reservation> existingReservations = reservationDao.findAll().stream()
                .filter(reservation -> reservation.getSlot().getId().equals(newReservation.getSlot().getId()) &&
                                     reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .collect(java.util.stream.Collectors.toList());

        return existingReservations.stream()
                .anyMatch(reservation -> 
                    (newReservation.getStartTime().isBefore(reservation.getEndTime()) && 
                     newReservation.getEndTime().isAfter(reservation.getStartTime())));
    }

    /**
     * Thread-safe slot release
     */
    @Transactional
    public void releaseSlotSafely(Long slotId) {
        ReentrantLock lock = getLockForSlot(slotId);
        
        lock.lock();
        try {
            releaseSlotWithRetry(slotId, 3);
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLockForSlot(Long slotId) {
        Slot slot = slotDao.findById(slotId);
        return "2W".equals(slot.getType()) ? twoWLock : fourWLock;
    }

    private void releaseSlotWithRetry(Long slotId, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                Slot slot = slotDao.findById(slotId);
                if (slot != null) {
                    slot.setOccupied(false);
                    slot.setVehicle(null);
                    slotDao.update(slot);
                }
                return;
            } catch (StaleObjectStateException e) {
                if (attempt == maxRetries - 1) {
                    throw new RuntimeException("Failed to release slot after " + maxRetries + " attempts due to concurrency conflicts");
                }
                try {
                    Thread.sleep(50 + (attempt * 25));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during slot release retry");
                }
            }
        }
    }

    /**
     * Get current lock status for monitoring
     */
    public String getLockStatus() {
        return String.format("Lock Status:\n" +
                "2W Lock: %s (Queue: %d)\n" +
                "4W Lock: %s (Queue: %d)",
                twoWLock.isLocked() ? "LOCKED" : "FREE",
                twoWLock.getQueueLength(),
                fourWLock.isLocked() ? "LOCKED" : "FREE",
                fourWLock.getQueueLength());
    }

    /**
     * Check if system is under high concurrency load
     */
    public boolean isHighLoad() {
        return twoWLock.getQueueLength() > 5 || fourWLock.getQueueLength() > 5;
    }
}
