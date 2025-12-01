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

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(WaitQueue waitQueue) {
        getSession().persist(waitQueue);
    }

    public void update(WaitQueue waitQueue) {
        getSession().merge(waitQueue);
    }

    public WaitQueue findById(Long id) {
        return getSession().get(WaitQueue.class, id);
    }

    public List<WaitQueue> findAll() {
        return getSession().createQuery("from WaitQueue", WaitQueue.class).list();
    }

    public void delete(WaitQueue waitQueue) {
        getSession().remove(waitQueue);
    }
}
