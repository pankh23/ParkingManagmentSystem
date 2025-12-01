package com.apc.parking.repository;

import com.apc.parking.model.ParkingLot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ParkingLotDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public ParkingLot save(ParkingLot parkingLot) {
        getSession().persist(parkingLot);
        return parkingLot;
    }

    public ParkingLot update(ParkingLot parkingLot) {
        return (ParkingLot) getSession().merge(parkingLot);
    }

    public ParkingLot findById(Long id) {
        return getSession().get(ParkingLot.class, id);
    }

    public List<ParkingLot> findAll() {
        return getSession().createQuery("from ParkingLot", ParkingLot.class).list();
    }

    public List<ParkingLot> findActiveLots() {
        return getSession().createQuery("from ParkingLot where isActive = true", ParkingLot.class).list();
    }

    public void delete(ParkingLot parkingLot) {
        getSession().remove(parkingLot);
    }

    public ParkingLot findByName(String name) {
        return getSession().createQuery("from ParkingLot where name = :name", ParkingLot.class)
                .setParameter("name", name)
                .uniqueResult();
    }
}
