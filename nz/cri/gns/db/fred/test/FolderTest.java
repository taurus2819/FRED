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
import nz.cri.gns.db.fred.AccessDeniedException;
import nz.cri.gns.db.fred.Folder;
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
public class FolderTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user, user2;

	public FolderTest(String arg0)
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
			this.user2 = new User("ben", "St.Bathans", ipConn);
		} catch (Exception e) {
		}
	}

	public void _testPooling() throws NotBoundException, SQLException, IOException {
		Folder.purge();
		Folder sv1 = Folder.getFolder(18, this.user, this.state);
		Folder sv2 = Folder.getFolder(18, this.user, this.state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, Folder.getPoolSize());
		Folder sv3 = Folder.getFolder(8, this.user, this.state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, Folder.getPoolSize());
		Folder sv4 = Folder.getFolder(18, this.user, this.state);
		assertEquals(2, Folder.getPoolSize());
	}

	public void _testFolderName() throws SQLException, IOException {
		Folder.purge();
		Folder sv = Folder.getFolder(18, this.user, this.state);
		String foldName = sv.getAsString(Folder.NAME);
		assertNotNull(foldName);
		assertEquals("Ben2 folder", foldName);
	}
	
	public void testFolderRights() throws SQLException, IOException {
		Folder.purge();
		Folder sv = Folder.getFolder(18, this.user, this.state);
		//Folder sv2 = Folder.getFolder(18, this.user2, this.state);
		int rights = sv.getUserRights();
		//int rights2 = sv.getUserRights();
		assertTrue(rights > 0);
		//assertTrue(rights2 > 0);
		System.out.println(rights);
		//System.out.println(rights2);
	}	

}
