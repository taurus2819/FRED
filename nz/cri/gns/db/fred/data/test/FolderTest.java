/*
 * Created on 25/03/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.db.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.Iterator;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.data.Feature;
import nz.cri.gns.db.fred.data.Folder;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

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
			this.user2 = new User("test", "test", ipConn);
		} catch (Exception e) {
		}
	}

	public void _testPooling() throws NotBoundException, SQLException, IOException {
		Folder.purge();
		Folder sv1 = new Folder(18, user, state);
		Folder sv2 = new Folder(18, user2, state);
		assertEquals(sv1.toString(), sv2.toString());
		assertEquals(1, Folder.getPoolSize());
		Folder sv3 = new Folder(8, user, state);
		assertNotSame(sv1.toString(), sv3.toString());
		assertEquals(2, Folder.getPoolSize());
		Folder sv4 = new Folder(18, user, state);
		assertEquals(2, Folder.getPoolSize());
	}

	public void _testFolderName() throws IOException, SQLException, InvalidCredentialsException {
		Folder.purge();
		Folder sv = new Folder(18, user2, state);
		String foldName = sv.getAsString(Folder.NAME);
		assertNotNull(foldName);
		assertEquals("Ben2 folder", foldName);
	}
	
	public void _testFolderRights() throws SQLException, IOException {
		Folder.purge();
		Folder sv = new Folder(18, user, state);
		Folder sv2 = new Folder(18, user2, state);
		assertEquals(sv.getUserRights(), 63);
		assertEquals(sv2.getUserRights(), 1);
		assertTrue(sv.isAllowedAdmin());
		assertFalse(sv2.isAllowedAdmin());
		assertTrue(sv.isAllowedAdmin());
	}	

	public void testMultSampName() throws SQLException, IOException, InvalidCredentialsException {
		Feature feature;
		Folder.purge();
		//Feature.purge();
		Folder f = new Folder(12, user, state);
		assertEquals(f.getLocalityCount(), 20);
		for (Iterator i = f.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
			feature = new Feature(((Integer) i.next()).intValue(), user, state);
			System.out.println(feature.getAsString(Feature.FEATURE_ID) + " : " + feature.getAsString(Feature.STATUS) + " : " + Feature.getPoolSize());
		}
	}

}
