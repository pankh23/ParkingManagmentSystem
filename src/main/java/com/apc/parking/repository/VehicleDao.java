package com.apc.parking.repository;

import com.apc.parking.model.Vehicle;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class VehicleDao {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Vehicle vehicle) {
        sessionFactory.getCurrentSession().save(vehicle);
    }

    public void update(Vehicle vehicle) {
        sessionFactory.getCurrentSession().update(vehicle);
    }

    public Vehicle findById(Long id) {
        return sessionFactory.getCurrentSession().get(Vehicle.class, id);
    }

    public List<Vehicle> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Vehicle", Vehicle.class).list();
    }
}