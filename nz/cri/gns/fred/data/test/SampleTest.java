package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

public class SampleTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user, user2;

	public SampleTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		Sample.purge();
		this.state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
			this.user2 = new User("test", "test", ipConn);
		} catch (Exception e) {
		}
	}

	public void testSample() throws SQLException, IOException, InvalidCredentialsException {
		Sample.purge();
		Sample s1 = new Sample(83465, this.user, this.state);
		System.out.println(s1.getAsString(Sample.SAMPLE_NAME));
	}
	
	public void _testPooling() throws NotBoundException, SQLException, IOException {
		Sample.purge();
		Sample sv1 = new Sample(390, this.user, this.state);
		Sample sv2 = new Sample(390, this.user2, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, Sample.getPoolSize());
		Sample sv3 = new Sample(391, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, Sample.getPoolSize());
		Sample sv4 = new Sample(390, this.user, this.state);
		assertEquals(2, Sample.getPoolSize());
		Sample sv5 = new Sample(390, null, this.state);
		assertEquals(2, Sample.getPoolSize());
		assertEquals(sv1.toString(), sv5.toString());
	}
	
	public void _testFields() throws IOException, SQLException, InvalidCredentialsException {
		Sample.purge();
		Sample sv = new Sample(601, this.user, this.state);
		String frNum = sv.getAsString(Sample.FR_NUMBER);
		assertNotNull(frNum);
		//assertEquals("Q22/f7733", frNum);
		System.out.println(sv.getAsInt(Sample.ACCURACY));
	}
	
	public void _testRestrictions() throws SQLException, IOException, InvalidCredentialsException {
		String test = null;
		Sample.purge();
		Sample f = new Sample(390, null, state);
		try {
			test = f.getAsString(Sample.FEATURE_TYPE);
		} catch (Exception e) {}
		assertNotNull(test);
		test = null;
		try {
			test = f.getAsString(Sample.LOCALITY);
		} catch (Exception e) {}
		assertNull(test);
		System.out.println(f.getAsString(Sample.FEATURE_SECURITY_CLASS_ID));
	}
	
	public void _testMultipleUsers() throws SQLException, IOException {
		Sample.purge();
		Sample f = new Sample(390, null, state);
		Sample f2 = new Sample(390, user, state);
		assertFalse(f.isUserAuthenticated());
		assertTrue(f2.isUserAuthenticated());		
	}
	
	public void _testDrillholeSamples() throws SQLException, IOException, InvalidCredentialsException {
		Sample.purge();
		Sample f = new Sample(3, this.user, this.state);
		Sample bf = FREDUtils.getSampleBelow(f, user, this.state);
		assertEquals(4, bf.getAsInt(Sample.SAMPLE_ID));
		Sample af = FREDUtils.getSampleAbove(f, user, this.state);
		assertEquals(2, af.getAsInt(Sample.SAMPLE_ID));
		assertEquals(3, Sample.getPoolSize());
	}

	public void _testStartDate() throws SQLException, IOException, InvalidCredentialsException {
		Sample.purge();
		Sample f = new Sample(1165, user, state);
		String test1 = f.getAsString(Sample.DATUM_TYPE);
		String test = f.getAsString(Sample.START_DATE_ROUNDING);
		System.out.println(test1);
	}

	public void _testRecords() throws SQLException, IOException, InvalidCredentialsException {
		Sample s = new Sample(1262, user, state);
		System.out.println(s.getAsString(Sample.RECORDS));
	}
}
