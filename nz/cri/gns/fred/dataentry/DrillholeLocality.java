package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class DrillholeLocality extends Locality {

	private String personID;
	
	public DrillholeLocality(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, "Drillhole", state);
	}
	
	public DrillholeLocality(int id, User user, PageState state) throws IOException,	SQLException, DataInputException, InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals("Drillhole")) throw new DataInputException("Feature Type", "Invalid");
		setField(OPERATING_COMPANY, sample.getAsString(Sample.PERSON));
		savedFlag = true;
	}

	protected void parseField(int field, String value) throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case OPERATING_COMPANY :
					rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(value.trim()));
					if (!rs.next())	throw new DataInputException("Operating Company", "Invalid value");
					personID = rs.getString(1);
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();	
		}
	}

	public void save() throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			super.save();
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.executeUpdate("UPDATE Feature SET Person_ID = " + JspUtils.sqlEscape(personID) + " WHERE Feature_ID = " + featureID);
			conn.releaseStatement();
		}
		savedFlag = true;
	}

}
