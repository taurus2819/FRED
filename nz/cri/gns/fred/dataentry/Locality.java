package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;


public abstract class Locality implements DataEntryForm {

	public static final int FIELD_NUMBER = 0;
	public static final int DRILLHOLE_NAME = 0;
	public static final int SECTION_NAME = 0;
	public static final int REGISTRATION_AREA = 1;
	public static final int WORKING_COMMENTS = 2;
	public static final int GRID_REF = 3;
	public static final int METHOD = 4;
	public static final int ACCURACY = 5;
	public static final int LOCALITY_DESC = 6;
	public static final int RECOLLECTION = 7;
	public static final int SIDETRACK = 7;

	private String[] fields = new String[7];
	private PageState state;

	public Locality(PageState state) {
		this.state = state;
	}
	
	public Locality(int id, User user, PageState state) throws IOException, SQLException, InvalidCredentialsException {
		this(state);
		Sample sample= new Sample(id, user, state);
		fields[0] = sample.getAsString(Sample.FEATURE_NAME);
		fields[1] = sample.getAsString(Sample.REG_AREA_ID);
		fields[2] = sample.getAsString(Sample.WORKING_COMMENTS);
		int origSystemID = sample.getAsInt(Sample.ORIG_SYSTEM_ID);
		if (origSystemID == 38) {
			fields[3] = "NZMG:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*');
		} else if (origSystemID == 16) {
			fields[3] = "TruncNZMG:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*');
		} else if (origSystemID == 29) {
			fields[3] = "LatLong:" + sample.getAsString(Sample.COUNTRY_CODE) + "*" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*');
		}
		fields[4] = sample.getAsString(Sample.METHOD_ID);
		fields[5] = sample.getAsString(Sample.ACCURACY);
		fields[6] = sample.getAsString(Sample.LOCALITY);
		fields[7] = sample.getAsString(Sample.RECOLLECTION_NUMBER);
	}

	public void setField(int field, String value) {
		fields[field] = value;
	}

	public void parseField(int field) throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			switch (field) {
				case 1:
					ResultSet rs = conn.executeQuery("SELECT * FROM Lookup WHERE Lookup_ID = " + fields[1] + " AND FieldName = 'RegAreaID'");
					if (!rs.next()) throw new DataInputException("Registration Area", "Invalid value");
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();
		}
	}

	public boolean saveData() {
		return false;
	}
	
	public boolean submitData() {
		return false;
	}
	
}
