package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.jsp.PageState;

public class OutcropLocality extends Locality {

	public static final int TEST = 8;

	private String[] fields = new String[9];

	public OutcropLocality(int id, User user, PageState state) throws IOException, SQLException, InvalidCredentialsException {
		super(id, user, state);
		fields[8] = "test";
	}

	public void parseField(int field) throws DataInputException {
		// TODO Auto-generated method stub
	}

	public boolean saveData() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean submitData() {
		// TODO Auto-generated method stub
		return false;
	}

}
