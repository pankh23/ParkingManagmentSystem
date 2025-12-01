package com.apc.parking.repository;

import com.apc.parking.model.Vehicle;
import org.hibernate.Session;
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

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // ✅ Fixed: Now returns the saved/updated vehicle
    public Vehicle save(Vehicle vehicle) {
        if(vehicle.getId() == null) {
            getSession().persist(vehicle); // new vehicle - ID gets generated
            return vehicle; // Return the vehicle with generated ID
        } else {
            return (Vehicle) getSession().merge(vehicle); // existing vehicle - merge returns the managed entity
        }
    }

    // ✅ Fixed: Now returns the updated vehicle
    public Vehicle update(Vehicle vehicle) {
        return (Vehicle) getSession().merge(vehicle);
    }

    public Vehicle findById(Long id) {
        return getSession().get(Vehicle.class, id);
    }

    public List<Vehicle> findAll() {
        return getSession().createQuery("from Vehicle", Vehicle.class).list();
    }

    public void delete(Vehicle vehicle) {
        getSession().remove(vehicle);
    }
}