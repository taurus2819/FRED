package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.jsp.PageState;


public class DataEntryFormFactory {

	public static Locality getLocalityDataEntryForm(int id, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		Feature feature = new Feature(id, user, state);
	  	if (feature.getAsString(Feature.FEATURE_TYPE).equals("Outcrop")) {
	  		return new OutcropLocality(id, user, state);
	  	} else if (feature.getAsString(Feature.FEATURE_TYPE).equals("Drillhole")) {
	  		return new DrillholeLocality(id, user, state);
		} else if (feature.getAsString(Feature.FEATURE_TYPE).equals("VertSect")) {
			return new VertSectLocality(id, user, state);
	  	} else {
	  		throw new DataInputException("Feature Type", "Invalid");
	  	}
	}

 	 public static Locality getLocalityDataEntryForm(String type, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		if (type.equals("Outcrop")) {
			return new OutcropLocality(user, folderID, state);
		} else if (type.equals("Drillhole")) {
			return new DrillholeLocality(user, folderID, state);
		} else if (type.equals("VertSect")) {
			return new VertSectLocality(user, folderID, state);
		} else {
			throw new DataInputException("Feature Type", "Invalid");
		}
	}
	
	public static Locality copyLocalityDataEntryForm(int copyID, int toID, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		Locality copyLoc = getLocalityDataEntryForm(copyID, user, state);
		Locality toLoc = getLocalityDataEntryForm(toID, user, state);
		for (int i = 0; i < copyLoc.getFieldCount(); i++) {
			toLoc.setField(i, copyLoc.getField(i));
		}
		return toLoc;
	}
	
	public static Locality copyLocalityDataEntryForm(int copyID, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		Locality copyLoc = getLocalityDataEntryForm(copyID, user, state);
		Locality toLoc = getLocalityDataEntryForm(copyLoc.getFeatureType(), user, folderID, state);
		for (int i = 0; i < copyLoc.getFieldCount(); i++) {
			toLoc.setField(i, copyLoc.getField(i));
		}
		return toLoc;
	}
	
}
