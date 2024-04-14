/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;
/**
 *
 * @author sitikond
 */
public class Dao {
    public static <T> void saveObject(T object, SessionFactory sessionFactory){
        try {
            Session session = sessionFactory.openSession();
            session.save(object);
            session.close();
        } catch (HibernateException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static <T> List<T> getObjectList(String query,SessionFactory sessionFactory){
        try{
            Session session = sessionFactory.openSession();
            List<T> list = (List<T>) session.createQuery(query).list();     
            session.close();
            return list;
        }catch(HibernateException ex){
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static <T> T getObject(int id, Class<T> clazz, SessionFactory sessionFactory){        
        T object = null;
        try{
            Session session = sessionFactory.openSession();
            object = (T) session.get(clazz, id);
            session.close();
        }catch(HibernateException ex){
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return object;
    }
    
//    public void closeSession() {
//        try {
//            Session sesh = session.get();
//            session.set(null);
//            if (sesh != null) {
//                sesh.close();
//            }
//        } catch (HibernateException e) {
//            log.log(Level.WARNING, null, e);
//            throw new RuntimeException(e);
//        }
//    }
    
}
