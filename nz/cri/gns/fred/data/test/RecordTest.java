package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.AdoptionRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

public class RecordTest extends TestCase {

	TestingPageState state;
	DBConnection conn;
	User user;

	public RecordTest(String arg0)
		throws NotBoundException, IOException, SQLException {
		super(arg0);
		this.state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
		} catch (Exception e) {
		}
	}


	public void _testPooling() throws NotBoundException, SQLException, IOException, InvalidCredentialsException {
		Record record = Record.getData(1280, user, state);
	}
	
	public void testToString() throws SQLException, IOException, InvalidCredentialsException {
		Record record = AdoptionRecord.getData(241, user, state);
		System.out.println(record);
	}
	
}