package com.apc.parking.service;

import com.apc.parking.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@apcparking.com}")
    private String fromEmail;

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public void init() {
        if (mailSender == null) {
            System.out.println("Mail sender not configured. Email notifications will be logged to console.");
        }
    }

    public void sendReservationCreatedNotification(Reservation reservation) {
        String subject = "Parking Reservation Created - Payment Required";
        String message = buildReservationCreatedMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendReservationConfirmedNotification(Reservation reservation) {
        String subject = "Parking Reservation Confirmed";
        String message = buildReservationConfirmedMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendReservationCancelledNotification(Reservation reservation) {
        String subject = "Parking Reservation Cancelled";
        String message = buildReservationCancelledMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendReservationExpiredNotification(Reservation reservation) {
        String subject = "Parking Reservation Expired";
        String message = buildReservationExpiredMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendReservationCompletedNotification(Reservation reservation) {
        String subject = "Parking Reservation Completed";
        String message = buildReservationCompletedMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendReservationReminderNotification(Reservation reservation) {
        String subject = "Parking Reservation Reminder";
        String message = buildReservationReminderMessage(reservation);
        
        if (emailEnabled && mailSender != null) {
            sendEmail(reservation.getUser().getUsername() + "@example.com", subject, message);
        } else {
            System.out.println("=== EMAIL NOTIFICATION ===");
            System.out.println("To: " + reservation.getUser().getUsername() + "@example.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    public void sendAdminQueueUpdateNotification(String message) {
        String subject = "Parking Queue Update";
        
        if (emailEnabled && mailSender != null) {
            sendEmail("admin@apcparking.com", subject, message);
        } else {
            System.out.println("=== ADMIN NOTIFICATION ===");
            System.out.println("To: admin@apcparking.com");
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("========================");
        }
    }

    private void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    private String buildReservationCreatedMessage(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        return String.format("Dear %s,\n\n" +
                "Your parking reservation has been created successfully!\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "Vehicle: %s\n" +
                "Start Time: %s\n" +
                "End Time: %s\n" +
                "Total Amount: ₹%.2f\n\n" +
                "Please complete your payment to confirm the reservation.\n" +
                "Payment Link: [Payment Gateway]\n\n" +
                "Thank you for choosing APC Parking!\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getVehicle().getId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter),
                reservation.getTotalAmount());
    }

    private String buildReservationConfirmedMessage(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        return String.format("Dear %s,\n\n" +
                "Your parking reservation has been confirmed!\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "Vehicle: %s\n" +
                "Start Time: %s\n" +
                "End Time: %s\n" +
                "Total Amount: ₹%.2f\n\n" +
                "Please arrive on time for your reservation.\n\n" +
                "Thank you for choosing APC Parking!\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getVehicle().getId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter),
                reservation.getTotalAmount());
    }

    private String buildReservationCancelledMessage(Reservation reservation) {
        return String.format("Dear %s,\n\n" +
                "Your parking reservation has been cancelled.\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "Refund Amount: ₹%.2f\n\n" +
                "If you have any questions, please contact our support team.\n\n" +
                "Thank you for choosing APC Parking!\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getTotalAmount());
    }

    private String buildReservationExpiredMessage(Reservation reservation) {
        return String.format("Dear %s,\n\n" +
                "Your parking reservation has expired.\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "End Time: %s\n\n" +
                "The slot has been released for other users.\n" +
                "If you need parking, please make a new reservation.\n\n" +
                "Thank you for choosing APC Parking!\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private String buildReservationCompletedMessage(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        return String.format("Dear %s,\n\n" +
                "Your parking reservation has been completed successfully!\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "Vehicle: %s\n" +
                "Start Time: %s\n" +
                "End Time: %s\n" +
                "Total Amount: ₹%.2f\n\n" +
                "Thank you for using our parking service!\n" +
                "We hope you had a great experience.\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getVehicle().getId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter),
                reservation.getTotalAmount());
    }

    private String buildReservationReminderMessage(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        return String.format("Dear %s,\n\n" +
                "This is a reminder for your upcoming parking reservation.\n\n" +
                "Reservation Details:\n" +
                "Reservation ID: %d\n" +
                "Parking Lot: %s\n" +
                "Slot: %s\n" +
                "Start Time: %s\n" +
                "End Time: %s\n\n" +
                "Please arrive on time for your reservation.\n\n" +
                "Thank you for choosing APC Parking!\n\n" +
                "Best regards,\n" +
                "APC Parking Management System",
                reservation.getUser().getUsername(),
                reservation.getId(),
                reservation.getSlot().getParkingLot().getName(),
                reservation.getSlot().getSlotNumber(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter));
    }
}
