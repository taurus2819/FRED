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
import nz.cri.gns.fred.FolderUtils;
import nz.cri.gns.fred.data.Feature;
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

	TestingPageState state;
	DBConnection conn;
	User user, testUser;

	public static final int TEST_DRILLHOLE = 1658;

	public LocalityTest(String arg0) throws NotBoundException, IOException, SQLException {
		super(arg0);
		this.state = new TestingPageState();
		//this.state.setInstance("gns");
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		try {
			this.user = new User("pseudo_ben", "santor32", ipConn);
			this.testUser = new User("test", "test", ipConn);
		} catch (Exception e) {}
	}

	public void testAllowedEdit() throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(1704, user, state);
	}
	
	public void __testCopy() throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(Feature.DRILLHOLE_LOCALITY, user, 12, state);
		System.out.println(form.getField(DataEntryForm.FEATURE_NAME));
		System.out.println(form.getField(DataEntryForm.SPUD_DATE));
		form.copyFrom(661);
		System.out.println(form.getField(DataEntryForm.FEATURE_NAME));
		System.out.println(form.getField(DataEntryForm.SPUD_DATE));
	}

	public void _testEdit() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm loc2 = DataEntryFormFactory.getLocalityDataEntryForm(1630, user, state);
		loc2.setTempField(DataEntryForm.COLLECTORS, "Morrison, Ben");
		loc2.setTempField(DataEntryForm.GRID_REF, "NZGD49:AU*-30*160");
		loc2.setFieldsFromTemp();
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
		loc.submit();
	}
	
	public void _testSite() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(1402, user, state);
		form.setField(DataEntryForm.GRID_REF, "AUCK:289400*680300");
		//form.setField(DataEntryForm.GRID_REF, "NZMG:2181000*5461000");
		//form.setField(DataEntryForm.GRID_REF, "LL49:NZ*-45*165");
		//form.setField(DataEntryForm.GRID_REF, "LL2000:NZ*-45*165");
		form.setField(DataEntryForm.METHOD, null);
		form.setField(DataEntryForm.ACCURACY, null);
		form.setField(DataEntryForm.LOCALITY_DESC, null);
		form.save();		
	}
	
	public void _testFeatName() throws NotBoundException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(Feature.OUTCROP_LOCALITY,user,221,state);
		form.save();	
	}
	
	public void _testMFEdit() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(901,user,state);
		System.out.println(form.getField(DataEntryForm.FEATURE_NAME));
		form.setField(DataEntryForm.COLUMN_MAP, "blah blah");
		form.save();
	}
	
	public void _testSubmit() throws NotBoundException, NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("test", "test", ipConn);
		FolderUtils.submitLocality(String.valueOf(TEST_DRILLHOLE), user, state);
	}
	
	public void _testOutcropSave() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(Feature.OUTCROP_LOCALITY, user, 221, state);
		form.setTempField(DataEntryForm.FEATURE_NAME, "New Outcrop 11");
		form.setTempField(DataEntryForm.COLLECTION_DATE, "25/12/2004");
		form.setFieldsFromTemp();
		form.save();		
	}
	
	public void _testCreateDeleteDrillholeLocality() throws SQLException, IOException, NotBoundException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		DataEntryForm form = DataEntryFormFactory.getLocalityDataEntryForm(Feature.DRILLHOLE_LOCALITY, user, 18, state);
		form.setTempField(DataEntryForm.FEATURE_NAME, "JUnit Drillhole");
		form.setTempField(DataEntryForm.COLLECTION_DATE, "10/2004");
		form.setTempField(DataEntryForm.DATUM_TYPE, "KB");
		form.setTempField(DataEntryForm.TERMINATION_DEPTH, "25");
		form.setFieldsFromTemp();
		int drillholeID = form.save();
		Feature feature = new Feature(drillholeID, user, state, true);
		assertEquals(feature.getAsString(Feature.FEATURE_NAME), "JUnit Drillhole");
		assertNull(feature.get(Feature.DRILLHOLE_LICENCE_NAME));
		assertEquals(feature.getAsString(Feature.DATUM_TYPE), "KB");
		form = DataEntryFormFactory.getLocalityDataEntryForm(drillholeID, user, state);
		form.delete();
		try {
			feature = new Feature(drillholeID, user, state, true);
		} catch (Exception e) {
			feature = null;
		}
		assertNull(feature);
	}

}
