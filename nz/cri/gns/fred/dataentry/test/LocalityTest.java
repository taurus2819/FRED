/*
 * Created on 19/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.fred.dataentry.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.dataentry.DataEntryForm;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.Locality;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
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

	public static void _testSave() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn =
			JspUtils.createDatabaseConnection(
				state.getSession(),
				"nz.cri.gns.db.fred.test.ipConn",
				"ip",
				state.getContext());
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm loc2 = DataEntryFormFactory.getLocalityDataEntryForm(661, user, state);
		loc2.setField(Locality.SPUD_DATE, "4/2003");
		loc2.setField(Locality.COMPLETION_DATE, "1/5/2003");
		loc2.setField(Locality.DATUM_TYPE, "KB");
		loc2.setField(Locality.DATUM_ELEVATION, "50.4");
		loc2.setField(Locality.KICK_OFF_DEPTH, "223.56");
		loc2.setField(Locality.TERMINATION_DEPTH, "500");
		loc2.save();
	}
	
	public static void testCopy() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn =
			JspUtils.createDatabaseConnection(
				state.getSession(),
				"nz.cri.gns.db.fred.test.ipConn",
				"ip",
				state.getContext());
		User user = new User("pseudo_ben", "santor32", ipConn);
		Locality loc = DataEntryFormFactory.getLocalityDataEntryForm(1282, user, state);
		//loc.setField(Locality.GRID_REF, "TruncNZMG:D39*1300*2100");
		for (int i = 0; i < loc.getFieldCount(); i++) {
			System.out.println(i + ": " + loc.getField(i));
		}
		System.out.println(loc.save());
		loc.makeDataEntryHTML(new PrintWriter(System.out));
		//Locality loc2 = LocalityFactory.copyLocality(661, 1282, user, state);
		//for (int i = 0; i < loc2.getFieldCount(); i++) {
		//	System.out.println(i + ": " + loc2.getField(i));
		//}
		//System.out.println(loc2.save());
	}

}
