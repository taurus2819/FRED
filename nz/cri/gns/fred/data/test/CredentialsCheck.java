package nz.cri.gns.fred.data.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.test.TestingPageState;
import nz.cri.gns.test.TestingServletContext;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.DBUtils;
/**
 *
 */
public class CredentialsCheck {

	public static void main(String[] args) throws IllegalArgumentException, InvalidCredentialsException, SQLException, ClassNotFoundException, NotBoundException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
		TestingPageState state = new TestingPageState();
		((TestingServletContext)state.context).setInitParameter("dbConnect", "raptor:1521:gns");
		
		//Test as me
		DBConnection fredConn = FREDUtils.getFREDConnection(state);
		DatabaseApp2 app = DBUtils.createDatabaseApp2("ip", "gns", "");
		User meUser = new User("iainm", "murtle", app);
		
		PaleontologyRecord rec = new PaleontologyRecord(102012, state);
		System.out.println("Iain:");
		System.out.println("Locality: " + FREDUtils.isAllowedLocality(meUser, rec.getAsString(Record.FEATURE_STATUS), rec.getAsString(Record.FEATURE_ID), state));
		System.out.println("Sample: " + FREDUtils.isAllowedSample(meUser, rec.getAsString(Record.SAMPLE_SECURITY_CLASS_ID), rec.getAsString(Record.STATUS), rec.getAsString(Record.SAMPLE_ID), state));
		System.out.println("Record: " + FREDUtils.isAllowedRecord(meUser, rec.getAsString(Record.SECURITY_CLASS_ID), rec.getAsString(Record.STATUS), rec.getAsString(Record.RECORD_ID), state));

		User pamUser = new User("pchester@actrix.co.nz", "d3e6c50", app);
		System.out.println("Pam:");
		System.out.println("Locality: " + FREDUtils.isAllowedLocality(meUser, rec.getAsString(Record.FEATURE_STATUS), rec.getAsString(Record.FEATURE_ID), state));
		System.out.println("Sample: " + FREDUtils.isAllowedSample(meUser, rec.getAsString(Record.SAMPLE_SECURITY_CLASS_ID), rec.getAsString(Record.STATUS), rec.getAsString(Record.SAMPLE_ID), state));
		System.out.println("Record: " + FREDUtils.isAllowedRecord(meUser, rec.getAsString(Record.SECURITY_CLASS_ID), rec.getAsString(Record.STATUS), rec.getAsString(Record.RECORD_ID), state));
		
		
	}
}
