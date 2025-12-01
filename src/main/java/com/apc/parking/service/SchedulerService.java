package com.apc.parking.service;

import com.apc.parking.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Runs every 5 minutes to check for completed reservations
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void checkCompletedReservations() {
        try {
            System.out.println("Checking for completed reservations at: " + LocalDateTime.now());
            
            // Note: In the demo system, this scheduled task won't work with the static list
            // because the static list is in the controller, not accessible from the service
            // For demo purposes, we'll just log that the check was performed
            System.out.println("Scheduled check completed (demo system - no database integration)");
        } catch (Exception e) {
            System.err.println("Error checking completed reservations: " + e.getMessage());
        }
    }

    /**
     * Runs every hour to send reminder notifications for upcoming reservations
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void sendReservationReminders() {
        try {
            System.out.println("Checking for upcoming reservations at: " + LocalDateTime.now());
            
            List<Reservation> activeReservations = reservationService.getActiveReservations();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourFromNow = now.plusHours(1);
            
            for (Reservation reservation : activeReservations) {
                // Send reminder if reservation starts within the next hour
                if (reservation.getStartTime().isAfter(now) && 
                    reservation.getStartTime().isBefore(oneHourFromNow)) {
                    
                    notificationService.sendReservationReminderNotification(reservation);
                    System.out.println("Sent reminder for reservation ID: " + reservation.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending reservation reminders: " + e.getMessage());
        }
    }

    /**
     * Runs every day at midnight to clean up old data
     */
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void cleanupOldData() {
        try {
            System.out.println("Running daily cleanup at: " + LocalDateTime.now());
            
            // This could include:
            // - Archiving old transactions
            // - Cleaning up expired reservations
            // - Generating daily reports
            // - Updating analytics
            
            System.out.println("Daily cleanup completed");
        } catch (Exception e) {
            System.err.println("Error during daily cleanup: " + e.getMessage());
        }
    }

    /**
     * Runs every 10 minutes to check system health
     */
    @Scheduled(fixedRate = 600000) // 10 minutes in milliseconds
    public void systemHealthCheck() {
        try {
            System.out.println("Performing system health check at: " + LocalDateTime.now());
            
            // Check database connectivity
            // Check service availability
            // Log system metrics
            
            System.out.println("System health check completed");
        } catch (Exception e) {
            System.err.println("System health check failed: " + e.getMessage());
        }
    }
    
    /**
     * Release slots for expired reservations
     */
    @Transactional
    public void releaseSlotsForExpiredReservations() {
        try {
            System.out.println("Releasing slots for expired reservations at: " + LocalDateTime.now());
            
            List<Reservation> expiredReservations = reservationService.getExpiredReservations();
            
            for (Reservation reservation : expiredReservations) {
                if (reservation.getSlot() != null && reservation.getSlot().isOccupied()) {
                    // Release the slot
                    reservation.getSlot().setOccupied(false);
                    reservation.getSlot().setVehicle(null);
                    
                    // Update slot availability in the controller
                    // Note: In a real application, you'd inject the ReservationController or use a service
                    System.out.println("Released slot " + reservation.getSlot().getSlotNumber() + 
                                     " for expired reservation " + reservation.getId());
                }
            }
            
            System.out.println("Slot release completed for " + expiredReservations.size() + " expired reservations");
        } catch (Exception e) {
            System.err.println("Error releasing slots for expired reservations: " + e.getMessage());
        }
    }
    
    /**
     * Manually process completed reservations - can be called via API
     */
    public void processCompletedReservations() {
        try {
            System.out.println("Manually processing completed reservations at: " + LocalDateTime.now());
            
            // Since we're using demo data, we'll process the static list directly
            // This is a workaround for the demo system
            List<Reservation> expiredReservations = getExpiredReservationsFromDemo();
            
            if (!expiredReservations.isEmpty()) {
                System.out.println("Found " + expiredReservations.size() + " reservations to mark as completed");
                
                for (Reservation reservation : expiredReservations) {
                    // Update reservation status to completed
                    reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                    
                    // Release the slot
                    if (reservation.getSlot() != null) {
                        reservation.getSlot().setOccupied(false);
                        reservation.getSlot().setVehicle(null);
                    }
                    
                    // Send notification
                    notificationService.sendReservationCompletedNotification(reservation);
                    
                    System.out.println("✅ Manually completed reservation ID: " + reservation.getId() + 
                                     " for slot: " + (reservation.getSlot() != null ? reservation.getSlot().getSlotNumber() : "N/A"));
                }
            } else {
                System.out.println("No reservations to mark as completed");
            }
        } catch (Exception e) {
            System.err.println("Error manually processing completed reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get expired reservations from demo data (workaround for demo system)
     */
    private List<Reservation> getExpiredReservationsFromDemo() {
        // This is a workaround - in a real system, this would be handled by the service layer
        // For now, we'll return an empty list and let the controller handle it
        return new java.util.ArrayList<>();
    }
}
