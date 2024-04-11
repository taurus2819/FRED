/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
/**
 *
 * @author sitikond
 */
public class Dao {
    public static <T> void saveObject(T object, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(object);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static <T> List<T> getObjectList(String query,SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List<T> list = (List<T>) session.createQuery(query).list();
            tx.commit();
            return list;
        }catch(HibernateException e){
            e.printStackTrace();
        }finally {
            session.close();
        }
        return null;
    }
    
    public static <T> T getObject(int id, Class<T> clazz, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        T object = null;
        try{
            tx = session.beginTransaction();
            object = (T) session.get(clazz, id);
            tx.commit();
        }catch(HibernateException e){
            e.printStackTrace();
        }finally {
            session.close();
        }
        return object;
    }
    
}
