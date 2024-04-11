/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings;

import org.hibernate.SessionFactory;
import nz.cri.gns.fred.hibernate.Feature;
import nz.cri.gns.fred.test.hibernatmappings.dao.Dao;
import nz.cri.gns.fred.util.SiteModelUtil;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sitikond
 */
public class FeatureTest {
    
    @Test
    public void testFeature(){
        SessionFactory sessionFactory = HibernateSessionFactoryUtil.buildSessionFactory();
        
        Feature feature = Dao.getObject(126205, Feature.class, sessionFactory);
        System.out.println("Feature site id = " + feature.getSiteId());
        System.out.println("Siteview = " + SiteModelUtil.getSiteView(feature.getSiteId()));
        
    }
    
}
