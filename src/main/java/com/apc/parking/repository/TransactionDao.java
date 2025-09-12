package com.apc.parking.repository;

import com.apc.parking.model.Transaction;
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

    public void save(Transaction transaction) {
        sessionFactory.getCurrentSession().save(transaction);
    }

    public List<Transaction> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Transaction", Transaction.class).list();
    }
}