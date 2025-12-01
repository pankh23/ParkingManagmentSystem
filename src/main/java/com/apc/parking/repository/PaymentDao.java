package com.apc.parking.repository;

import com.apc.parking.model.Payment;
import com.apc.parking.model.Reservation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PaymentDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Payment save(Payment payment) {
        getSession().persist(payment);
        return payment;
    }

    public Payment update(Payment payment) {
        return (Payment) getSession().merge(payment);
    }

    public Payment findById(Long id) {
        return getSession().get(Payment.class, id);
    }

    public List<Payment> findAll() {
        return getSession().createQuery("from Payment", Payment.class).list();
    }

    public Payment findByReservation(Reservation reservation) {
        return getSession().createQuery("from Payment where reservation = :reservation", Payment.class)
                .setParameter("reservation", reservation)
                .uniqueResult();
    }

    public Payment findByRazorpayPaymentId(String razorpayPaymentId) {
        return getSession().createQuery("from Payment where razorpayPaymentId = :paymentId", Payment.class)
                .setParameter("paymentId", razorpayPaymentId)
                .uniqueResult();
    }

    public Payment findByRazorpayOrderId(String razorpayOrderId) {
        return getSession().createQuery("from Payment where razorpayOrderId = :orderId", Payment.class)
                .setParameter("orderId", razorpayOrderId)
                .uniqueResult();
    }

    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return getSession().createQuery("from Payment where status = :status", Payment.class)
                .setParameter("status", status)
                .list();
    }

    public void delete(Payment payment) {
        getSession().remove(payment);
    }
}
