package nz.cri.gns.fred.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Hibernate does not like inter-schema stuff, so this class provides methods for
 * ip.person interaction
 */
public class UserUtil {

    public String getUserName(int userId) throws NamingException, SQLException {
        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT given_name || ' ' || family_name FROM ip.person WHERE person_id = " + userId);
        String name = (rs.next()) ? rs.getString(1) : "";
        rs.close();
        statement.close();
        conn.close();
        return name;
    }

    /**
     * @return
     * @throws NamingException
     * @throws SQLException
     */
    private Connection getConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        Context ctx = (Context)context.lookup("comp/env");
        DataSource source = (DataSource)ctx.lookup("jdbc/fr");
        
        return source.getConnection();
    }
}
