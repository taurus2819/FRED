/*
 * Created on 25/03/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.db.fred.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.FolderList;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;
import junit.framework.TestCase;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
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
					"nz.cri.gns.db.fred.test.ipConn",
					"ip",
					state.getContext());
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
		} catch (Exception e) {
		}
	}

	public void testFolderList() throws IOException, SQLException {
		FolderList fl = new FolderList(user, state);
		System.out.println(fl.getPersonalFolderCount());
		assertEquals(fl.getPersonalFolderCount(), 4);
		assertEquals(fl.getAdminFolderCount(), 11);
	}

}
