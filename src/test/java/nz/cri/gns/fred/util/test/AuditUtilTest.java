package nz.cri.gns.fred.util.test;

import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.dialect.Oracle9Dialect;
import net.sf.hibernate.odmg.Name;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.dbcp.GOLDataSourceFactory;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.util.AuditUtil;
import junit.framework.TestCase;

public class AuditUtilTest extends TestCase {

	private DAOFactory factory;

	// TODO Find a way to set up context
	public void setUp() {			

	}

	public void tearDown() {
		factory = null;
	}

}
