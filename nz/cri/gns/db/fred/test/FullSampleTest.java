package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.AccessDeniedException;
import nz.cri.gns.db.fred.FREDUtils;
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


public void testPooling() throws NotBoundException, SQLException, IOException, AccessDeniedException {
	FullSample.purge();
	FullSample sv1 = FullSample.getFullSample(390, this.user, this.state);
	FullSample sv2 = FullSample.getFullSample(390, this.user, this.state);
	assertEquals(sv1.toString(), sv2.toString());
	assertEquals(1, FullSample.getPoolSize());
	FullSample sv3 = FullSample.getFullSample(391, this.user, this.state);
	assertNotSame(sv1.toString(), sv3.toString());
	assertEquals(2, FullSample.getPoolSize());
	FullSample sv4 = FullSample.getFullSample(390, this.user, this.state);
	assertEquals(2, FullSample.getPoolSize());
	FullSample sv5 = FullSample.getFullSample(390, null, this.state);
	assertEquals(2, FullSample.getPoolSize());
	assertEquals(sv1.toString(), sv5.toString());
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

public void testRestrictions() throws SQLException, IOException, AccessDeniedException {
	String test = null;
	FullSample.purge();
	FullSample f = FullSample.getFullSample(390, null, this.state);
	try {
		test = f.getAsString(FullSample.FEATURE_TYPE);
	} catch (Exception e) {}
	assertNotNull(test);
	test = null;
	try {
		test = f.getAsString(FullSample.LOCALITY);
	} catch (Exception e) {}
	assertNull(test);
	System.out.println(f.getAsString(FullSample.SECURITY_CLASS_ID));
}

public void testDrillholeSamples() throws SQLException, IOException, AccessDeniedException {
	FullSample.purge();
	FullSample f = FullSample.getFullSample(3, this.user, this.state);
	FullSample bf = FREDUtils.getSampleBelow(f, user, this.state);
	assertEquals(4, bf.getAsInt(FullSample.SAMPLE_ID));
	FullSample af = FREDUtils.getSampleAbove(f, user, this.state);
	assertEquals(2, af.getAsInt(FullSample.SAMPLE_ID));
	assertEquals(3, FullSample.getPoolSize());
}

}
