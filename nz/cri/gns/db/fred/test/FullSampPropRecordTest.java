package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.AccessDeniedException;
import nz.cri.gns.db.fred.FullSampPropRecord;
import nz.cri.gns.db.fred.FullSample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class FullSampPropRecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public FullSampPropRecordTest(String arg0)
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
		} catch (Exception e) {
		}
	}


public void testPooling() throws NotBoundException, SQLException, IOException, AccessDeniedException {
	FullSampPropRecord.purge();
	FullSampPropRecord sv1 = FullSampPropRecord.getFullSampPropRecord(390, this.user, this.state);
	FullSampPropRecord sv2 = FullSampPropRecord.getFullSampPropRecord(390, this.user, this.state);
	assertEquals(sv1.toString(), sv2.toString());
	assertEquals(1, FullSampPropRecord.getPoolSize());
	FullSampPropRecord sv3 = FullSampPropRecord.getFullSampPropRecord(391, this.user, this.state);
	assertNotSame(sv1.toString(), sv3.toString());
	assertEquals(2, FullSampPropRecord.getPoolSize());
	FullSampPropRecord sv4 = FullSampPropRecord.getFullSampPropRecord(390, this.user, this.state);
	assertEquals(2, FullSampPropRecord.getPoolSize());
}

public void testFRNum() throws SQLException, IOException, AccessDeniedException {
	FullSampPropRecord.purge();
	FullSampPropRecord sv = FullSampPropRecord.getFullSampPropRecord(390, this.user, this.state);
	String sampName = sv.getAsString(FullSampPropRecord.SAMPLE_NAME);
	assertNotNull(sampName);
	assertEquals("Q22/f7733", sampName);
	System.out.println(sampName);
}

}