package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class FeatureTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user, user2;

	public FeatureTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		Feature.purge();
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


	public void testPooling() throws NotBoundException, SQLException, IOException {
		Feature.purge();
		Feature sv1 = new Feature(509, this.user, this.state);
		Feature sv2 = new Feature(509, this.user2, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, Feature.getPoolSize());
		Feature sv3 = new Feature(507, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, Feature.getPoolSize());
		Feature sv4 = new Feature(509, this.user, this.state);
		assertEquals(2, Feature.getPoolSize());
		Feature sv5 = new Feature(509, null, this.state);
		assertEquals(2, Feature.getPoolSize());
		assertEquals(sv1.toString(), sv5.toString());
	}
	
	public void testSamples() throws SQLException, IOException, InvalidCredentialsException {
		assertEquals(2, Feature.getPoolSize());	
		Feature.purge();
		Feature f = new Feature(1163, user, state);
		Feature dhole = new Feature(1, user, state);
		assertEquals(f.getSampleCount(), 2);
		assertEquals(dhole.getSampleCount(), 5);
	}
	
	public void _testRestrictions() throws SQLException, IOException, InvalidCredentialsException {
		String test = null;
		Feature.purge();
		Feature f = new Feature(509, null, state);
		try {
			test = f.getAsString(Feature.FEATURE_TYPE);
		} catch (Exception e) {}
		assertNotNull(test);
		test = null;
		try {
			test = f.getAsString(Feature.LOCALITY);
		} catch (Exception e) {}
		assertNull(test);
		System.out.println(f.getAsString(Feature.SECURITY_CLASS_ID));
	}
	
	public void _testWorkingFeature() throws SQLException, IOException, InvalidCredentialsException {
		String test = null;
		Feature.purge();
		Feature f = new Feature(1163, user, state);
/*		try {
			test = f.getAsString(Feature.FEATURE_TYPE);
		} catch (Exception e) {}
		assertNotNull(test);
		test = null;
		try {
			test = f.getAsString(Feature.LOCALITY);
		} catch (Exception e) {}
		assertNull(test);
*/		
		System.out.println(FREDUtils.getUserWorkingLocalityRights(user, "1163", state));
		System.out.println(f.isUserAuthenticated());
		//System.out.println(f.getAsString(Feature.STATUS));
		//System.out.println(f.getAsString(Feature.SECURITY_CLASS_ID));
	}
	
	public void _testMultipleUsers() throws SQLException, IOException {
		Feature.purge();
		Feature f = new Feature(562, null, state);
		Feature f2 = new Feature(562, user, state);
		Feature f3 = new Feature(509, null, state);
		Feature f4 = new Feature(509, user, state);
		Feature f5 = new Feature(509, user2, state);
		assertFalse(f.isUserAuthenticated());
		assertTrue(f2.isUserAuthenticated());
		assertFalse(f3.isUserAuthenticated());
		assertTrue(f4.isUserAuthenticated());
		assertFalse(f5.isUserAuthenticated());		
	}

}
