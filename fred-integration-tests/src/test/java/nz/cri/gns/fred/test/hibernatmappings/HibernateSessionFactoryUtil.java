/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author sitikond
 */
public class HibernateSessionFactoryUtil {
    
    public static SessionFactory buildSessionFactory() {
        //load hibernate configuration from XML file
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                                                        .configure("frpostgres.cfg.xml").build();
        try{
            //create sessionFactory from the hibernate configuration
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }catch(Exception e){
            StandardServiceRegistryBuilder.destroy(registry);
            e.printStackTrace();
            throw new RuntimeException("Error building sessionfactory");
        }
    }
    
}
