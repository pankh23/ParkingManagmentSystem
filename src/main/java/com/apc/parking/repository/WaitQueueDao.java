package com.apc.parking.repository;

import com.apc.parking.model.WaitQueue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class WaitQueueDao {

    @Autowired
    private SessionFactory sessionFactory;

    // Helper method to get current session
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Save a new WaitQueue entry
    public void save(WaitQueue waitQueue) {
        getSession().persist(waitQueue); // persist replaces deprecated save()
    }

    // Fetch all WaitQueue entries
    public List<WaitQueue> findAll() {
        return getSession().createQuery("from WaitQueue", WaitQueue.class).list();
    }
}
