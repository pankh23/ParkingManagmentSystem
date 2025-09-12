package com.apc.parking.repository;

import com.apc.parking.model.Slot;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SlotDao {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Slot slot) {
        sessionFactory.getCurrentSession().save(slot);
    }

    public void update(Slot slot) {
        sessionFactory.getCurrentSession().update(slot);
    }

    public Slot findById(Long id) {
        return sessionFactory.getCurrentSession().get(Slot.class, id);
    }

    public List<Slot> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Slot", Slot.class).list();
    }
}