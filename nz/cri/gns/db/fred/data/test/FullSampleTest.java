package nz.cri.gns.db.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.db.fred.data.AccessDeniedException;
import nz.cri.gns.db.fred.data.FullSample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class FullSampleTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user, user2;

	public FullSampleTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		FullSample.purge();
		this.state = new TestingPageState();
			DBConnection ipConn =
				JspUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.db.fred.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
			this.user2 = new User("test", "test", ipConn);
		} catch (Exception e) {
		}
	}


	public void testPooling() throws NotBoundException, SQLException, IOException, AccessDeniedException {
		FullSample.purge();
		FullSample sv1 = new FullSample(390, this.user, this.state);
		FullSample sv2 = new FullSample(390, this.user2, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, FullSample.getPoolSize());
		FullSample sv3 = new FullSample(391, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, FullSample.getPoolSize());
		FullSample sv4 = new FullSample(390, this.user, this.state);
		assertEquals(2, FullSample.getPoolSize());
		FullSample sv5 = new FullSample(390, null, this.state);
		assertEquals(2, FullSample.getPoolSize());
		assertEquals(sv1.toString(), sv5.toString());
	}
	
	public void testFRNum() throws IOException, SQLException, AccessDeniedException, InvalidCredentialsException {
		FullSample.purge();
		FullSample sv = new FullSample(390, this.user, this.state);
		String frNum = sv.getAsString(FullSample.FR_NUMBER);
		assertNotNull(frNum);
		assertEquals("Q22/f7733", frNum);
	}
	
	public void testRestrictions() throws SQLException, IOException, AccessDeniedException, InvalidCredentialsException {
		String test = null;
		FullSample.purge();
		FullSample f = new FullSample(390, null, state);
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
	
	public void testMultipleUsers() throws SQLException, IOException {
		FullSample.purge();
		FullSample f = new FullSample(390, null, state);
		FullSample f2 = new FullSample(390, user, state);
		assertFalse(f.isAuthenticated());
		assertTrue(f2.isAuthenticated());		
	}
	
	public void testDrillholeSamples() throws SQLException, IOException, AccessDeniedException, InvalidCredentialsException {
		FullSample.purge();
		FullSample f = new FullSample(3, this.user, this.state);
		FullSample bf = FREDUtils.getSampleBelow(f, user, this.state);
		assertEquals(4, bf.getAsInt(FullSample.SAMPLE_ID));
		FullSample af = FREDUtils.getSampleAbove(f, user, this.state);
		assertEquals(2, af.getAsInt(FullSample.SAMPLE_ID));
		assertEquals(3, FullSample.getPoolSize());
	}

}
