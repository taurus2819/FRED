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
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.AccessDeniedException;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.RecordDE;
import nz.cri.gns.fred.dataentry.SampPropRecordDE;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RecordTest extends TestCase {

	public static void testLoad() throws NotBoundException, InvalidCredentialsException, IllegalArgumentException, SQLException, IOException, AccessDeniedException, DataInputException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		RecordDE record = new SampPropRecordDE(400, user, state);
		//loc.setField(Locality.GRID_REF, "TruncNZMG:D39*1300*2100");
		for (int i = 0; i < record.getFieldCount(); i++) {
			System.out.println(i + ": " + record.getField(i));
		}
		//record.makeNavPanelHTML(new PrintWriter(System.out));
		//Locality loc2 = LocalityFactory.copyLocality(661, 1282, user, state);
		//for (int i = 0; i < loc2.getFieldCount(); i++) {
		//	System.out.println(i + ": " + loc2.getField(i));
		//}
		//System.out.println(loc2.save());
	}

}
