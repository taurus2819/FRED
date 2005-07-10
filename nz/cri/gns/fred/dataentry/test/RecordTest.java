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
import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.dataentry.DataEntryForm;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.TaxonomicListException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RecordTest extends TestCase {

	public void _testDataEntryForm() throws NotBoundException, InsufficientPrivelegesException, IOException, DataInputException, TaxonomicListException, SQLException, InvalidCredentialsException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("test", "test", ipConn);
		DataEntryForm form = DataEntryFormFactory.getRecordDataEntryForm(301, user, state);
		for (int i = 0; i < form.getFieldCount(); i++) {
			System.out.println(i + ": " + form.getField(i));
		}
		form.setField(DataEntryForm.HARDNESS, "152");
		//form.save();
	}

	public void testPalLists() throws NotBoundException, InsufficientPrivelegesException, IOException, DataInputException, TaxonomicListException, SQLException, InvalidCredentialsException {
		PaleontologyRecord.purge();
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("pseudo_ben", "santor32", ipConn);
		DataEntryForm form = DataEntryFormFactory.getRecordDataEntryForm(1807, user, state);
		for (int i = 0; i < form.getFieldCount(); i++) {
			System.out.println(i + ": " + form.getField(i));
		}
		form.setTempField(DataEntryForm.IDENTIFICATION_DATE, "21/1/2005");
		form.setTempField(DataEntryForm.TAXA_LIST, "FUNGI*Tony Blair****");
		try {
			form.setFieldsFromTemp();
		} catch (Exception e) {}
		for (int i = 0; i < form.getFieldCount(); i++) {
			System.out.println(i + ": " + form.getField(i));
		}
		//form.save();
	}
	
}
