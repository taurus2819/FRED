package nz.cri.gns.fred.reports;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.util.NullOutputStream;

public abstract class AbstractReport {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.reports.AbstractReport");

    protected static final void setupJNDI(String host, String sid, String user, String password) {
        try {
            JNDI.setup();

        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            if (ex instanceof IllegalStateException) {
                if ("InitialContextFactoryBuilder already set".equals(ex.getMessage())) {
                    System.out.println("Using previous JNDI setup");
                    return;
                }
            }
        }

        try {
            InitialContext context = new InitialContext();
            final Connection conn = DBUtils.getJavaSqlConnection(host, sid, user, password);
            FredHibernate.get().configure(conn);
            // TODO: Why are we fudging with the DataSource like this? -mikevdg.
            context.bind("java:comp/env/jdbc/fr", new DataSource() {

                public int getLoginTimeout() throws SQLException {
                    return 0;
                }

                public void setLoginTimeout(int seconds) throws SQLException {
                }

                public void setLogWriter(PrintWriter out) throws SQLException {
                }

                public PrintWriter getLogWriter() throws SQLException {
                    return new PrintWriter(new NullOutputStream());
                }

                public Connection getConnection(String username, String password)
                        throws SQLException {
                    return null;
                }

                public Connection getConnection() throws SQLException {
                    return UnclosableConnection.create(conn);
                }

                @Override
                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return conn.isWrapperFor(iface);
                }

                @Override
                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return conn.unwrap(iface);
                }

                @Override
                public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });

        } catch (ClassNotFoundException | SQLException | NamingException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
