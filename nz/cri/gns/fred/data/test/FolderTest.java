/*
 * Created on 25/03/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.Iterator;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FolderUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Folder;
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

	public static final int TEST_FOLDER = 323;

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

	public void testCreateFolder() throws SQLException, IOException {
		FolderUtils.addFolder("JUnit Test Folder", user2, state);
	}


	public void _testFolderName() throws IOException, SQLException, InvalidCredentialsException {
		Folder sv = new Folder(18, user2, state);
		String foldName = sv.getAsString(Folder.NAME);
		assertNotNull(foldName);
		assertEquals("Ben2 folder", foldName);
	}
	
	public void _testFolderRights() throws SQLException, IOException {
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
		//Feature.purge();
		Folder f = new Folder(12, user, state);
		assertEquals(f.getLocalityCount(), 20);
		for (Iterator i = f.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
			feature = new Feature(((Integer) i.next()).intValue(), user, state);
			//System.out.println(feature.getAsString(Feature.FEATURE_ID) + " : " + feature.getAsString(Feature.STATUS) + " : " + Feature.getPoolSize());
		}
	}

}
