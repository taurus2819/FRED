/*
 * Created on 19/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.fred.dataentry.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.Locality;
import nz.cri.gns.fred.dataentry.LocalityFactory;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.test.TestingPageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LocalityTest extends TestCase {

	public static void testCreate() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn =
			JspUtils.createDatabaseConnection(
				state.getSession(),
				"nz.cri.gns.db.fred.test.ipConn",
				"ip",
				state.getContext());
		User user = new User("pseudo_ben", "santor32", ipConn);

		//Locality loc = new Locality(user, 221, "Outcrop", state);
		//loc.setField(0, "Test3");
		//loc.save();
		Locality loc2 = LocalityFactory.getLocality("Drillhole", 661, user, state);
		loc2.setField(Locality.WORKING_COMMENTS, "Changed Again and again");
		loc2.setField(Locality.OPERATING_COMPANY, "Morrison, Bender");
		loc2.save();
	}

}
