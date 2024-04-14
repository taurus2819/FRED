/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
//import org.hibernate.SessionFactory;
import nz.cri.gns.fred.test.hibernatmappings.dao.Dao;
import nz.cri.gns.fred.util.SiteModelUtil;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sitikond
 */
public class FeatureTest {
    
    public static void main(String[] args){
        testFeature();
    }
    
    //@Test
    public static void testFeature(){
        try {
            SessionFactory sessionFactory = HibernateSessionFactoryUtil.buildSessionFactory();
            
            nz.cri.gns.fred.hibernate.FeatureTest1 feature = Dao.getObject(124648, nz.cri.gns.fred.hibernate.FeatureTest1.class, sessionFactory);
            System.out.println("Feature site id = " + feature.getSiteId());
            System.out.println("Siteview = " + SiteModelUtil.getSiteView(feature.getSiteId()).toString());
            
            sessionFactory.close();
        } catch (HibernateException ex) {
            Logger.getLogger(FeatureTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
