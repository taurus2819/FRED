package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.FolderList;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;
import junit.framework.TestCase;

public class FolderListTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public FolderListTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		this.state = new TestingPageState();
			DBConnection ipConn =
				JspUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.db.fred.data.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
		} catch (Exception e) {
		}
	}

	public void testFolderList() throws IOException, SQLException {
		FolderList fl = new FolderList(user, state);
		assertEquals(fl.getPersonalFolderCount(), 3);
		assertEquals(fl.getAdminFolderCount(), 2);
	}

}
