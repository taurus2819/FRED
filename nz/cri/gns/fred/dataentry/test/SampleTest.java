package nz.cri.gns.fred.dataentry.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

import junit.framework.TestCase;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.FolderUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.data.test.FolderTest;
import nz.cri.gns.fred.dataentry.DataEntryForm;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.TaxonomicListException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.test.TestingPageState;

public class SampleTest extends TestCase {

	public void _testAddDeleteSample() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("test", "test", ipConn);
		Feature feature = new Feature(LocalityTest.TEST_DRILLHOLE, user, state);
		int sampleID = feature.addNewSample("27.5", null, "11", String.valueOf(FolderTest.TEST_FOLDER));
		Sample sample = new Sample(sampleID, user, state, true);
		assertEquals(sample.getAsString(Sample.TOP_DEPTH), "27.5");
		assertNull(sample.get(Sample.BOTTOM_DEPTH));
		assertEquals(sample.getAsString(Sample.DRILL_TYPE), "Cutting");
		FolderUtils.deleteSample(String.valueOf(sampleID), user, state);	
	}
	
	public void testSampleProperties() throws NotBoundException, IOException, SQLException, InvalidCredentialsException, DataInputException, TaxonomicListException {
		TestingPageState state = new TestingPageState();
		DBConnection ipConn = FREDUtils.getIPConnection(state);
		User user = new User("test", "test", ipConn);
		Feature feature = new Feature(LocalityTest.TEST_DRILLHOLE, user, state);
		int sampleID = feature.addNewSample("10", "20", "11",String.valueOf(FolderTest.TEST_FOLDER));
		DataEntryForm form = DataEntryFormFactory.getSampleDataEntryForm(sampleID, user, state);
		form.setTempField(DataEntryForm.COLLECTION_DATE, "2004");
		form.setTempField(DataEntryForm.COLLECTORS, "Morrison, Ben");
		form.setTempField(DataEntryForm.FOSSILS_IN_PLACE, "Yes");
		form.setTempField(DataEntryForm.INF_AGE_START, "7");
		form.setTempField(DataEntryForm.INF_START_MOD, "?");
		form.setTempField(DataEntryForm.INF_AGE_STOP, "685");
		form.setTempField(DataEntryForm.PREVIOUS_SAMPLE, "d39/f0003");
		form.setFieldsFromTemp();
		form.save();
		Sample sample = new Sample(sampleID, user, state, true);
		assertEquals(sample.getAsString(Sample.COLLECTION_DATE), "2004-01-01");
		assertEquals(sample.getAsString(Sample.COLLECTION_DATE_ROUNDING), "Year");
		assertEquals(sample.getAsString(Sample.IN_PLACE), "Yes");
		assertNull(sample.get(Sample.COLUMN_MAP));
		assertNotNull(sample.get(Sample.RELATIONSHIP_NEARBY));
		FolderUtils.deleteSample(String.valueOf(sampleID), user, state);
	}
}
