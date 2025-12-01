package com.apc.parking.repository;

import com.apc.parking.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(User user) {
        getSession().persist(user);
    }

    public void update(User user) {
        getSession().merge(user);
    }

    public User findById(Long id) {
        return getSession().get(User.class, id);
    }

    public User findByUsername(String username) {
        return getSession()
                .createQuery("from User where username=:username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    public List<User> findAll() {
        return getSession().createQuery("from User", User.class).list();
    }

    public void delete(User user) {
        getSession().remove(user);
    }
}
