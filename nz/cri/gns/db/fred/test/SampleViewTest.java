package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.SampleView;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.test.TestingPageState;

public class SampleViewTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public SampleViewTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		SampleView.purge();
		this.state = new TestingPageState();
		this.conn =
			ExternalUtils.createDatabaseConnection(
				state.getSession(),
				"nz.cri.gns.db.fred.test.frConn",
				"fr",
				state.getContext());
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


public void testPooling() throws NotBoundException, IOException, SQLException {
	SampleView.purge();
	SampleView sv1 = SampleView.getSampleView(390, this.conn);
	SampleView sv2 = SampleView.getSampleView(390, this.conn);
	assertEquals(sv1.toString(), sv2.toString());
	assertEquals(1, SampleView.getPoolSize());
	SampleView sv3 = SampleView.getSampleView(391, this.conn);
	assertNotSame(sv1.toString(), sv3.toString());
	assertEquals(2, SampleView.getPoolSize());
}

public void testFRNum() throws SQLException {
	SampleView.purge();
	SampleView sv = SampleView.getSampleView(390, this.conn);
	String frNum = sv.getAsString(SampleView.FR_NUMBER, user);
	assertNotNull(frNum);
	assertEquals("Q22/f7733", frNum);
}

}
