/*
 * Created on 19/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.fred.dataentry.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dataentry.DataEntryForm;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.LocalityDE;
import nz.cri.gns.fred.dataentry.TaxonomicListException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LocalityTest extends TestCase {

	public void _testSave() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm loc2 = DataEntryFormFactory.getLocalityDataEntryForm(661, user, state);
		for (int i = 0; i < loc2.getFieldCount(); i++) {
			System.out.println(i + ": " + loc2.getField(i));
		}
		loc2.setField(LocalityDE.SPUD_DATE, "4/2003");
		loc2.setField(LocalityDE.COMPLETION_DATE, null);
		loc2.setField(LocalityDE.DATUM_TYPE, "KB");
		loc2.setField(LocalityDE.DATUM_ELEVATION, "50.4");
		loc2.setField(LocalityDE.KICK_OFF_DEPTH, "223.56");
		loc2.setField(LocalityDE.TERMINATION_DEPTH, "500");
		for (int i = 0; i < loc2.getFieldCount(); i++) {
			System.out.println(i + ": " + loc2.getField(i));
		}
		loc2.save();
	}
	
	public void _testCopy() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		LocalityDE loc = DataEntryFormFactory.getLocalityDataEntryForm(808, user, state);
		//loc.setField(Locality.GRID_REF, "TruncNZMG:D39*1300*2100");
		for (int i = 0; i < loc.getFieldCount(); i++) {
			System.out.println(i + ": " + loc.getField(i));
		}
		//System.out.println(loc.save());
		//loc.makeDataEntryHTML(new PrintWriter(System.out));
		//Locality loc2 = LocalityFactory.copyLocality(661, 1282, user, state);
		//for (int i = 0; i < loc2.getFieldCount(); i++) {
		//	System.out.println(i + ": " + loc2.getField(i));
		//}
		//System.out.println(loc2.save());
	}

	public void _testSecType() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		LocalityDE loc = DataEntryFormFactory.getLocalityDataEntryForm(1264, user, state);
		System.out.println(loc.getField(LocalityDE.SECURITY_TYPE));
		loc.setField(LocalityDE.SECURITY_TYPE, "23");
		loc.save();
		System.out.println(loc.getField(LocalityDE.SECURITY_TYPE));
	} 
	
	public void _testOutcrop() throws IOException, SQLException, DataInputException, InvalidCredentialsException, MalformedURLException, RemoteException, NotBoundException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		LocalityDE loc = DataEntryFormFactory.getLocalityDataEntryForm(1264, user, state);
		System.out.println(loc.getField(DataEntryForm.COLLECTION_DATE));
		//loc.setField(DataEntryForm.COLLECTION_DATE, "6/1999");
		//loc.save();
		loc.makeDataEntryHTML(new PrintWriter(System.out));
	}
	
	public void _testNotWorkingLoc() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		LocalityDE loc = DataEntryFormFactory.getLocalityDataEntryForm(1282, user, state);
		loc.revoke();
		loc.submit();
	}
	
	public void testSite() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(1402, user, state);
		//form.setField(DataEntryForm.GRID_REF, "TruncNZMG:F45*810*610");
		//form.setField(DataEntryForm.GRID_REF, "NZMG:2181000*5461000");
		form.setField(DataEntryForm.GRID_REF, "LatLong:NZ*-45*165");
		form.setField(DataEntryForm.METHOD, "3");
		form.setField(DataEntryForm.ACCURACY, "20");
		form.save();		
	}
}
