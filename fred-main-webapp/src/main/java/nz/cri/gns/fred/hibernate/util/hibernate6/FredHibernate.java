package nz.cri.gns.fred.hibernate.util.hibernate6;

import java.net.URL;
import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.fred.dao.DAOFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate provider used by the servlet application.
 *
 * <p>The provider deliberately keeps Hibernate Sessions thread-confined. This
 * is especially important when virtual threads are introduced elsewhere in
 * the application: an existing Session must never be passed from the servlet
 * request thread to another platform or virtual thread.</p>
 */
public class FredHibernate implements HibernateProvider {

    private static final Logger LOG = Logger.getLogger(FredHibernate.class.getName());

    private static volatile FredHibernate instance;

    private final SessionFactory sessionFactory;
    private final HibernateDAOFactory factory;
    private final ThreadLocal<Session> session = new ThreadLocal<>();
    private Connection connection;

    public FredHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "sessionFactory");
        this.factory = new HibernateDAOFactory(this);
    }

    public static FredHibernate get() {
        FredHibernate current = instance;
        if (current == null) {
            synchronized (FredHibernate.class) {
                current = instance;
                if (current == null) {
                    current = configure();
                    instance = current;
                }
            }
        }
        return current;
    }

    private static FredHibernate configure() {
        try {
            return buildJndiSessionFactory();
        } catch (RuntimeException primaryFailure) {
            LOG.log(Level.WARNING,
                "Primary JNDI Hibernate configuration failed; checking environment fallback",
                primaryFailure);
            try {
                return buildEnvironmentSessionFactory();
            } catch (RuntimeException fallbackFailure) {
                primaryFailure.addSuppressed(fallbackFailure);
                throw new IllegalStateException("Hibernate configuration failed completely",
                    primaryFailure);
            }
        }
    }

    private static FredHibernate buildJndiSessionFactory() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.datasource", "java:comp/env/jdbc/fr");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        SessionFactory factory = new Configuration()
            .setProperties(properties)
            .configure(getHibernateCfg())
            .buildSessionFactory();

        return new FredHibernate(factory);
    }

    /**
     * Optional development/emergency fallback. No credentials are stored in
     * source control. All four variables must be explicitly provided.
     */
    private static FredHibernate buildEnvironmentSessionFactory() {
        String url = requiredEnvironmentVariable("FRED_DB_URL");
        String username = requiredEnvironmentVariable("FRED_DB_USERNAME");
        String password = requiredEnvironmentVariable("FRED_DB_PASSWORD");
        String schema = System.getenv().getOrDefault("FRED_DB_SCHEMA", "fr");

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("hibernate.default_schema", schema);

        SessionFactory factory = new Configuration()
            .setProperties(properties)
            .configure(getHibernateCfg())
            .buildSessionFactory();

        return new FredHibernate(factory);
    }

    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }

    /**
     * Allows tests/tools to provide an explicit connection before calling get().
     */
    public static synchronized FredHibernate usingConnection(Connection connection) {
        FredHibernate result = buildEnvironmentSessionFactory();
        result.useConnection(connection);
        instance = result;
        return result;
    }

    private void useConnection(Connection connection) {
        this.connection = connection;
    }

    private static URL getHibernateCfg() {
        URL hibernateConfig = FredHibernate.class.getClassLoader()
            .getResource("hibernate/hibernate.cfg.xml");
        return Objects.requireNonNull(hibernateConfig,
            "hibernate/hibernate.cfg.xml is missing");
    }

    @Override
    public Session currentSession() {
        Session current = session.get();
        if (current == null) {
            try {
                current = sessionFactory.openSession();
                session.set(current);
            } catch (HibernateException e) {
                throw new IllegalStateException("Unable to open Hibernate session", e);
            }
        }
        return current;
    }

    @Override
    public void closeSession() {
        Session current = session.get();
        session.remove();

        if (current == null) {
            return;
        }

        try {
            current.close();
        } catch (HibernateException e) {
            LOG.log(Level.WARNING, "Unable to close Hibernate session", e);
            throw new IllegalStateException("Unable to close Hibernate session", e);
        }
    }

    public DAOFactory getDAOFactory() {
        return factory;
    }

    /**
     * Retained for compatibility with code/tests that inspect the explicit
     * connection configured through usingConnection().
     */
    Connection configuredConnection() {
        return connection;
    }
}
