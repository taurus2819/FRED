package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.SampPropRecord;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class SampPropRecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public SampPropRecordTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		Sample.purge();
		this.state = new TestingPageState();
			DBConnection ipConn =
				JspUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.db.fred.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
		} catch (Exception e) {
		}
	}


	public void _testPooling() throws NotBoundException, SQLException, IOException, InvalidCredentialsException {
		SampPropRecord.purge();
		SampPropRecord sv1 = (SampPropRecord) SampPropRecord.getData(390, this.user, this.state);
		SampPropRecord sv2 = (SampPropRecord) SampPropRecord.getData(390, this.user, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, SampPropRecord.getPoolSize());
		SampPropRecord sv3 = (SampPropRecord) SampPropRecord.getData(391, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, SampPropRecord.getPoolSize());
		SampPropRecord sv4 = (SampPropRecord) SampPropRecord.getData(390, this.user, this.state);
		assertEquals(2, SampPropRecord.getPoolSize());
	}
	
	public void _testFRNum() throws SQLException, IOException, InvalidCredentialsException {
		SampPropRecord.purge();
		SampPropRecord sv = (SampPropRecord) SampPropRecord.getData(390, this.user, this.state);
		String sampName = sv.getAsString(SampPropRecord.SAMPLE_NAME);
		assertNotNull(sampName);
		assertEquals("Q22/f7733", sampName);
		System.out.println(sampName);
	}
	
	public void _testAuthentication() throws SQLException, IOException, InvalidCredentialsException {
		SampPropRecord.purge();
		SampPropRecord sp = (SampPropRecord) SampPropRecord.getData(1140, user, state);
		SampPropRecord sp1 = (SampPropRecord) SampPropRecord.getData(1140, null, state);
		assertNotNull(sp);
		assertNull(sp1);
	}
	
	public void testFields() throws SQLException, IOException, InvalidCredentialsException {
		SampPropRecord.purge();
		SampPropRecord sp = (SampPropRecord) SampPropRecord.getData(1220, user, state);
		for (int i = 0; i < 80; i++) {
			System.out.println(i + ": " + sp.getAsString(i));
		}				
	}

}