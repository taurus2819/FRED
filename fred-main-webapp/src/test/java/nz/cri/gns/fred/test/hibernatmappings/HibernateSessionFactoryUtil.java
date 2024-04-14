/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.fred.test.hibernatmappings;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.SessionFactory;

//import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author sitikond
 */
public class HibernateSessionFactoryUtil {
    
    public static SessionFactory buildSessionFactory() {        
        
        Path filePath = Paths.get("frpostgres.cfg.xml");
        System.out.println("Path = " + filePath.toAbsolutePath());
        //load hibernate configuration from XML file
//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                                                        .configure("hibernate/frpostgres.cfg.xml").build();
//        try{
//            //create sessionFactory from the hibernate configuration
//            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
//        }catch(Exception e){
//            StandardServiceRegistryBuilder.destroy(registry);
//            e.printStackTrace();
//            throw new RuntimeException("Error building sessionfactory");
//        }
        Properties props = new Properties();        
        props.put("connection.datasource", "java:comp/env/jdbc/fr");
        props.put("default_schema", "fr");
        props.put("dialect", "net.sf.hibernate.dialect.PostgreSQLDialect");
        props.put("query.substitutions", "true=1, false=0");

        try {
            Configuration config = (new Configuration()).setProperties(props).configure(getHibernateCfg());
            return config.buildSessionFactory();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getHibernateCfg() {
        URL hibernateConfig = HibernateSessionFactoryUtil.class.getClassLoader().getResource("hibernate/frpostgres.cfg.xml");
        if (null == hibernateConfig) {
            throw new NullPointerException("hibernate/frpostgres.cfg.xml is missing.");
        }
        return hibernateConfig;
    }
    
}
