package com.apc.parking.service;

import com.apc.parking.model.Payment;
import com.apc.parking.model.Reservation;
import com.apc.parking.repository.PaymentDao;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Value("${razorpay.key_id:rzp_test_1234567890}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret:test_secret_1234567890}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    public PaymentService() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        } catch (RazorpayException e) {
            System.err.println("Failed to initialize Razorpay client: " + e.getMessage());
        }
    }

    public Payment createPayment(Reservation reservation) {
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getTotalAmount());
        payment.setDescription("Parking reservation for slot " + reservation.getSlot().getSlotNumber());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        Payment savedPayment = paymentDao.save(payment);

        // Create Razorpay order
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int)(reservation.getTotalAmount() * 100)); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "reservation_" + reservation.getId());
            orderRequest.put("notes", new JSONObject().put("reservation_id", reservation.getId()));

            Order order = razorpayClient.orders.create(orderRequest);
            
            savedPayment.setRazorpayOrderId(order.get("id"));
            savedPayment.setCurrency("INR");
            
            return paymentDao.update(savedPayment);
        } catch (RazorpayException e) {
            savedPayment.setStatus(Payment.PaymentStatus.FAILED);
            savedPayment.setFailureReason("Failed to create Razorpay order: " + e.getMessage());
            return paymentDao.update(savedPayment);
        }
    }

    public Payment updatePaymentStatus(Reservation reservation, String razorpayPaymentId, Payment.PaymentStatus status) {
        Payment payment = paymentDao.findByReservation(reservation);
        if (payment == null) {
            throw new RuntimeException("Payment not found for reservation");
        }

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setStatus(status);
        
        if (status == Payment.PaymentStatus.SUCCESSFUL) {
            payment.setPaidAt(LocalDateTime.now());
        }

        return paymentDao.update(payment);
    }

    public boolean verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        try {
            // In a real implementation, you would verify the signature using Razorpay's webhook verification
            // For now, we'll do a simple check
            Payment payment = paymentDao.findByRazorpayOrderId(razorpayOrderId);
            if (payment == null) {
                return false;
            }

            // Update payment with successful status
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setStatus(Payment.PaymentStatus.SUCCESSFUL);
            payment.setPaidAt(LocalDateTime.now());
            paymentDao.update(payment);

            return true;
        } catch (Exception e) {
            System.err.println("Payment verification failed: " + e.getMessage());
            return false;
        }
    }

    public Payment processRefund(Reservation reservation) {
        Payment payment = paymentDao.findByReservation(reservation);
        if (payment == null || payment.getStatus() != Payment.PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("No successful payment found for refund");
        }

        try {
            // In a real implementation, you would call Razorpay's refund API
            // For now, we'll just update the status
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            payment.setUpdatedAt(LocalDateTime.now());
            
            return paymentDao.update(payment);
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Refund failed: " + e.getMessage());
            return paymentDao.update(payment);
        }
    }

    public Payment getPaymentByReservation(Reservation reservation) {
        return paymentDao.findByReservation(reservation);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentDao.findById(paymentId);
    }

    public String getPaymentStatus(Long reservationId) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        
        Payment payment = paymentDao.findByReservation(reservation);
        if (payment == null) {
            return "No payment found for this reservation";
        }

        return String.format("Payment ID: %d\n" +
                "Amount: ₹%.2f\n" +
                "Status: %s\n" +
                "Payment Method: %s\n" +
                "Created At: %s\n" +
                "Paid At: %s",
                payment.getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A",
                payment.getCreatedAt(),
                payment.getPaidAt() != null ? payment.getPaidAt() : "N/A");
    }

    public String generatePaymentLink(Reservation reservation) {
        Payment payment = createPayment(reservation);
        
        // In a real implementation, you would generate a payment link
        // For console application, we'll return the order details
        return String.format("Payment Details:\n" +
                "Order ID: %s\n" +
                "Amount: ₹%.2f\n" +
                "Description: %s\n" +
                "Please complete payment using Razorpay gateway.",
                payment.getRazorpayOrderId(),
                payment.getAmount(),
                payment.getDescription());
    }
}
