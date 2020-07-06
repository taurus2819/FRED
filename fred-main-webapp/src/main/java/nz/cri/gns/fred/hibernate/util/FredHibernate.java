package nz.cri.gns.fred.hibernate.util;

import java.net.URL;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.hibernate.HibernateException;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.fred.dao.DAOFactory;

public class FredHibernate implements HibernateProvider {

    private static FredHibernate instance;
    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.hibernate.util.FredHibernate");

    public static FredHibernate get() {
        if (null == instance) {
            instance = configure(false);
        }
        return instance;
    }

    private static HibernateDAOFactory factory;

    private static SessionFactory sessionFactory;
    private final ThreadLocal<Session> session = new ThreadLocal<>();
    private Connection conn = null; // If we want to use this rather than JNDI.

    public FredHibernate(SessionFactory s) {
        this.sessionFactory = s;
        factory = new HibernateDAOFactory(this);
    }

    /**
     * Configure by pulling the database details from JNDI.
     */
    private static FredHibernate configure(boolean skipJNDI) {
        Properties props = new Properties();
        if (!skipJNDI) {
            props.put("hibernate.connection.datasource", "java:comp/env/jdbc/fr");
        } else {
            props.put("hibernate.default_schema", "FR");
        }
        props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
        props.put("query.substitutions", "true=1, false=0");

        try {
            Configuration config = (new Configuration()).setProperties(props).configure(getHibernateCfg());
            sessionFactory = config.buildSessionFactory();
            return new FredHibernate(sessionFactory);
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If you want to use a specific connection, then call me before calling
     * get();
     */
    public static FredHibernate usingConnection(Connection conn) {
        FredHibernate result = configure(true);
        result.useConnection(conn);
        instance = result;
        return result;
    }

    private void useConnection(Connection conn) {
        this.conn = conn;
    }

    private static URL getHibernateCfg() {
        URL hibernateConfig = FredHibernate.class.getClassLoader().getResource("hibernate/hibernate.cfg.xml");
        if (null == hibernateConfig) {
            throw new NullPointerException("hibernate/hibernate.cfg.xml is missing.");
        }
        return hibernateConfig;
    }

    @Override
    public Session currentSession() {
        if (null == session.get()) {
            try {
                if (null == conn) {
                    session.set(sessionFactory.openSession());
                } else {
                    session.set(sessionFactory.openSession(conn));
                }
            } catch (HibernateException e) {
                throw new RuntimeException(e);
            }
        }

        return session.get();
    }

    @Override
    public void closeSession() {
        try {
            Session sesh = session.get();
            session.set(null);
            if (sesh != null) {
                sesh.close();
            }
        } catch (HibernateException e) {
            log.log(Level.WARNING, null, e);
            throw new RuntimeException(e);
        }
    }

    public DAOFactory getDAOFactory() {
        return factory;
    }
}
