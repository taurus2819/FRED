package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.jsp.PageState;

public class OutcropLocality extends Locality {

	public OutcropLocality(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, "Outcrop", state);
	}
	
	public OutcropLocality(int id, User user, PageState state) throws IOException,	SQLException, DataInputException, InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals("Outcrop")) throw new DataInputException("Feature Type", "Invalid");
	}

}
