package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.jsp.PageState;


public class DataEntryFormFactory {

	public static LocalityDE getLocalityDataEntryForm(int featureID, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		Feature feature = new Feature(featureID, user, state);
	  	if (feature.getAsString(Feature.FEATURE_TYPE).equals(Feature.OUTCROP_LOCALITY)) {
	  		return new OutcropLocalityDE(featureID, user, state);
	  	} else if (feature.getAsString(Feature.FEATURE_TYPE).equals(Feature.DRILLHOLE_LOCALITY)) {
	  		return new DrillholeLocalityDE(featureID, user, state);
		} else if (feature.getAsString(Feature.FEATURE_TYPE).equals(Feature.VERTICAL_SECTION_LOCALITY)) {
			return new VertSectLocalityDE(featureID, user, state);
	  	} else {
	  		throw new DataInputException("Feature Type", "Invalid");
	  	}
	}

 	 public static LocalityDE getLocalityDataEntryForm(String type, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		if (type.equals(Feature.OUTCROP_LOCALITY)) {
			return new OutcropLocalityDE(user, folderID, state);
		} else if (type.equals(Feature.DRILLHOLE_LOCALITY)) {
			return new DrillholeLocalityDE(user, folderID, state);
		} else if (type.equals(Feature.VERTICAL_SECTION_LOCALITY)) {
			return new VertSectLocalityDE(user, folderID, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}
	}
	
	public static LocalityDE copyLocalityDataEntryForm(int copyID, int toID, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE copyLoc = getLocalityDataEntryForm(copyID, user, state);
		LocalityDE toLoc = getLocalityDataEntryForm(toID, user, state);
		for (int i = 0; i < copyLoc.getFieldCount(); i++) {
			toLoc.setField(i, copyLoc.getField(i));
		}
		return toLoc;
	}
	
	public static LocalityDE copyLocalityDataEntryForm(int copyID, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE copyLoc = getLocalityDataEntryForm(copyID, user, state);
		LocalityDE toLoc = getLocalityDataEntryForm(copyLoc.getFeatureType(), user, folderID, state);
		for (int i = 0; i < copyLoc.getFieldCount(); i++) {
			toLoc.setField(i, copyLoc.getField(i));
		}
		return toLoc;
	}
	
	public static SampleDE getSampleDataEntryForm(int sampleID, User user, PageState state) throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException  {
		return new SampleDE(sampleID, user, state);
	}
	
	public static SampleDE getSampleDataEntryForm(User user, int featureID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		return new SampleDE(user, featureID, folderID, state);
	}
	
	public static RecordDE getRecordDataEntryForm(int recordID, User user, PageState state) throws DataInputException, InvalidCredentialsException, SQLException, IOException {
		Record record = Record.getData(recordID, user, state);
		if (record.getAsString(Record.RECORD_TYPE).equals(Record.ADOPTION_RECORD)) {
			return new AdoptionRecordDE(recordID, user, state);
		} else if (record.getAsString(Record.RECORD_TYPE).equals(Record.PALEONTOLOGY_RECORD)) {
			return new PaleontologyRecordDE(recordID, user, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}
	}
	
	public static RecordDE getRecordDataEntryForm(String type, User user, int sampleID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		if (type.equals(Record.ADOPTION_RECORD)) {
			return new AdoptionRecordDE(user, sampleID, folderID, state);
		} else if (type.equals(Record.PALEONTOLOGY_RECORD)) {
			return new PaleontologyRecordDE(user, sampleID, folderID, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}	
	}
}
