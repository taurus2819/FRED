package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.jsp.PageState;


public class LocalityFactory {

  public static Locality getLocality(String type, int id, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
  	if (type.equals("Outcrop")) {
  		return new OutcropLocality(id, user, state);
  	} else if (type.equals("Drillhole")) {
  		return new DrillholeLocality(id, user, state);
  	} else {
  		throw new DataInputException("Feature Type", "Invalid");
  	}
  }

  public static Locality getLocality(String type, User user, int folderID, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
	if (type.equals("Outcrop")) {
		return new OutcropLocality(user, folderID, state);
	} else if (type.equals("Drillhole")) {
		return new DrillholeLocality(user, folderID, state);
	} else {
		throw new DataInputException("Feature Type", "Invalid");
	}
  }

}
