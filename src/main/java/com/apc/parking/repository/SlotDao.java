package com.apc.parking.repository;

import com.apc.parking.model.Slot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SlotDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Slot slot) {
        getSession().persist(slot);
    }

    public Slot update(Slot slot) {
        return (Slot) getSession().merge(slot);
    }

    public Slot findById(Long id) {
        return getSession().get(Slot.class, id);
    }

    public List<Slot> findAll() {
        return getSession().createQuery("from Slot", Slot.class).list();
    }

    public void delete(Slot slot) {
        getSession().remove(slot);
    }
}
