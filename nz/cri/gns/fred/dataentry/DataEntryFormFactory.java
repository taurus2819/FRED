package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.jsp.PageState;


public class DataEntryFormFactory {

	public static LocalityDE getLocalityDataEntryForm(int id, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		Feature feature = new Feature(id, user, state);
	  	if (feature.getAsString(Feature.FEATURE_TYPE).equals("Outcrop")) {
	  		return new OutcropLocalityDE(id, user, state);
	  	} else if (feature.getAsString(Feature.FEATURE_TYPE).equals("Drillhole")) {
	  		return new DrillholeLocalityDE(id, user, state);
		} else if (feature.getAsString(Feature.FEATURE_TYPE).equals("VertSect")) {
			return new VertSectLocalityDE(id, user, state);
	  	} else {
	  		throw new DataInputException("Feature Type", "Invalid");
	  	}
	}

 	 public static LocalityDE getLocalityDataEntryForm(String type, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		if (type.equals("Outcrop")) {
			return new OutcropLocalityDE(user, folderID, state);
		} else if (type.equals("Drillhole")) {
			return new DrillholeLocalityDE(user, folderID, state);
		} else if (type.equals("VertSect")) {
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
	
	public static RecordDE getRecordDataEntryForm(int id, User user, PageState state) throws DataInputException, InvalidCredentialsException, SQLException, IOException {
		Record record = Record.getData(id, user, state);
		if (record.getAsString(Record.RECORD_TYPE).equals("SMP")) {
			return new SampPropRecordDE(id, user, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}
	}
	
	public static RecordDE getRecordDataEntryForm(String type, User user, int sampleID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		if (type.equals("SMP")) {
			return new SampPropRecordDE(user, sampleID, folderID, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}	
	}
}
