package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.AccessDeniedException;
import nz.cri.gns.db.fred.FullSample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.test.TestingPageState;

public class FullSampleTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public FullSampleTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		FullSample.purge();
		this.state = new TestingPageState();
			DBConnection ipConn =
				ExternalUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.db.fred.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("ben", "St.Bathans", ipConn);
		} catch (Exception e) {
		}
	}


public void _testPooling() throws NotBoundException, SQLException, IOException, AccessDeniedException {
	FullSample.purge();
	FullSample sv1 = null, sv2 = null, sv3 = null, sv4 = null;
	try {
		sv1 = FullSample.getFullSample(390, this.user, this.state);
		sv2 = FullSample.getFullSample(390, this.user, this.state);
	} catch (Exception e) {}
	//assertEquals(sv1.toString(), sv2.toString());
	assertEquals(1, FullSample.getPoolSize());
	System.out.println(FullSample.getPoolSize());
	try {
		sv3 = FullSample.getFullSample(391, this.user, this.state);
	} catch (Exception e) {}
	//assertNotSame(sv1.toString(), sv3.toString());
	assertEquals(2, FullSample.getPoolSize());
	try {
		sv4 = FullSample.getFullSample(390, this.user, this.state);
	} catch (Exception e) {}
	assertEquals(2, FullSample.getPoolSize());
}

public void testFRNum() throws IOException, SQLException, AccessDeniedException {
	FullSample.purge();
	try {
	FullSample sv = FullSample.getFullSample(390, this.user, this.state);
	String frNum = sv.getAsString(FullSample.FR_NUMBER);
	assertNotNull(frNum);
	assertEquals("Q22/f7733", frNum);
	} catch (Exception e) {}
}

}
