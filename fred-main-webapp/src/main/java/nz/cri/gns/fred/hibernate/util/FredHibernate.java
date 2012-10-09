package nz.cri.gns.fred.hibernate.util;

import java.sql.Connection;
import net.sf.hibernate.Session;
import nz.cri.gns.dataaccess.HibernateConfiguration;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;

public class FredHibernate implements HibernateProvider {

	public static FredHibernate get() {
		return util;
	}
	
	private static final FredHibernate util;
	private static final HibernateDAOFactory factory;
	private HibernateConfiguration config;
	

    static {
    	util = new FredHibernate();
    	factory = new HibernateDAOFactory(util);
    }
    
    private FredHibernate() {
        config = new HibernateConfiguration();
        try {
            config.configureJNDI("fr", getClass().getClassLoader().getResource("hibernate/hibernate.cfg.xml"));
        } catch (StorageAccessException e) {
                e.printStackTrace();
        }
    }
    
    public void configure(Connection conn) {
        config.configure(conn, getClass().getClassLoader().getResource("hibernate/hibernate.cfg.xml"));
    }

    public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public Session currentSession() throws StorageAccessException {
    	return config.currentSession();
    }

    public void closeSession() throws StorageAccessException {
    	config.closeSession();
    }
    
    public DAOFactory getDAOFactory() {
    	return factory;
    }
}