package nz.cri.gns.db.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.data.AccessDeniedException;
import nz.cri.gns.db.fred.data.PaleontologyRecord;
import nz.cri.gns.db.fred.data.Record;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

public class RecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user, user2;

	public RecordTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		PaleontologyRecord.purge();
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
		Record.purge();
		Record sv1 = Record.getData(781, this.user, this.state);
		Record sv2 = Record.getData(781, this.user2, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, Record.getPoolSize());
		Record sv3 = Record.getData(223, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, Record.getPoolSize());
		Record sv4 = Record.getData(781, this.user, this.state);
		assertEquals(2, Record.getPoolSize());
	}
	
}