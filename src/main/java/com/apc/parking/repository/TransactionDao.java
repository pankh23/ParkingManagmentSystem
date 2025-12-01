package com.apc.parking.repository;

import com.apc.parking.model.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TransactionDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Transaction transaction) {
        getSession().persist(transaction);
    }

    public void update(Transaction transaction) {
        getSession().merge(transaction);
    }

    public Transaction findById(Long id) {
        return getSession().get(Transaction.class, id);
    }

    public List<Transaction> findAll() {
        return getSession().createQuery("from Transaction", Transaction.class).list();
    }

    public void delete(Transaction transaction) {
        getSession().remove(transaction);
    }
}
