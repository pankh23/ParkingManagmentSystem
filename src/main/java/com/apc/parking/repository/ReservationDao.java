package com.apc.parking.repository;

import com.apc.parking.model.Reservation;
import com.apc.parking.model.User;
import com.apc.parking.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ReservationDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Reservation save(Reservation reservation) {
        getSession().persist(reservation);
        return reservation;
    }

    public Reservation update(Reservation reservation) {
        return (Reservation) getSession().merge(reservation);
    }

    public Reservation findById(Long id) {
        return getSession().get(Reservation.class, id);
    }

    public List<Reservation> findAll() {
        return getSession().createQuery("from Reservation", Reservation.class).list();
    }

    public List<Reservation> findByUser(User user) {
        return getSession().createQuery("from Reservation where user = :user", Reservation.class)
                .setParameter("user", user)
                .list();
    }

    public List<Reservation> findByVehicle(Vehicle vehicle) {
        return getSession().createQuery("from Reservation where vehicle = :vehicle", Reservation.class)
                .setParameter("vehicle", vehicle)
                .list();
    }

    public List<Reservation> findActiveReservations() {
        return getSession().createQuery(
                "from Reservation where status = 'CONFIRMED' and startTime <= :now and endTime >= :now", 
                Reservation.class)
                .setParameter("now", LocalDateTime.now())
                .list();
    }

    public List<Reservation> findExpiredReservations() {
        return getSession().createQuery(
                "from Reservation where status = 'CONFIRMED' and endTime < :now", 
                Reservation.class)
                .setParameter("now", LocalDateTime.now())
                .list();
    }

    public List<Reservation> findPendingReservations() {
        return getSession().createQuery(
                "from Reservation where status = 'PENDING'", 
                Reservation.class)
                .list();
    }

    public void delete(Reservation reservation) {
        getSession().remove(reservation);
    }
}
