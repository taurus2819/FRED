package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.RoundedDate;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class VertSectLocality extends Locality {

	private String personID;
	private RoundedDate startDate;
	private RoundedDate compDate;
	
	public VertSectLocality(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, "VertSect", state);
	}
	
	public VertSectLocality(int id, User user, PageState state) throws IOException,	SQLException, DataInputException, InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals("VertSect")) throw new DataInputException("Feature Type", "Invalid");
		setField(SECTION_COLLECTOR, sample.getAsString(Sample.PERSON));
		setField(START_DATE, reverseParseDate(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)));
		setField(COMPLETION_DATE, reverseParseDate(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)));
		setField(DATUM_TYPE, sample.getAsString(Sample.DATUM_TYPE));
		setField(DATUM_ELEVATION, sample.getAsString(Sample.DATUM_ELEVATION));
		setField(TOP_HORIZON, sample.getAsString(Sample.START_DEPTH));
		setField(BASE_HORIZON, sample.getAsString(Sample.FINISH_DEPTH));
		savedFlag = true;
	}

	protected void parseField(int field, String value) throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case SECTION_COLLECTOR :
					rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(value.trim()));
					if (!rs.next())	throw new DataInputException("Section Collector", "Invalid value");
					personID = rs.getString(1);
					break;
				case START_DATE :
					startDate = FREDUtils.parseRoundedDate(value);
					break;
				case COMPLETION_DATE :
					compDate = FREDUtils.parseRoundedDate(value);
					break;
				case DATUM_TYPE :
					if (!(value.equals("Top") || value.equals("Bottom"))) throw new DataInputException("Datum Type", "Invalid Data");
					break;
				case DATUM_ELEVATION :
					if (!FREDUtils.isNumeric(value)) throw new DataInputException("Datum Elevation", "Invalid Data");
					break;
				case TOP_HORIZON :
					if (!FREDUtils.isNumeric(value)) throw new DataInputException("Top Horizon", "Invalid Data");
					break;
				case BASE_HORIZON :
					if (!FREDUtils.isNumeric(value)) throw new DataInputException("Base Horizon", "Invalid Data");
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();	
		}
	}

	public int save() throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate("UPDATE Feature SET Person_ID = " + JspUtils.sqlEscape(personID) + ", Start_Date = TO_DATE('" + startDate.getDateString() + "'), Start_Date_Rounding = " + JspUtils.sqlEscape(startDate.getDateRounding()) + ", Finish_Date = TO_DATE('" + compDate.getDateString() + "'), Finish_Date_Rounding = " + JspUtils.sqlEscape(compDate.getDateRounding()) + ", Datum_Type = " + JspUtils.sqlEscape(fields[DATUM_TYPE]) + ", Datum_Elevation = " + JspUtils.sqlEscape(fields[DATUM_ELEVATION]) + ", Start_Depth = " + JspUtils.sqlEscape(fields[TOP_HORIZON]) + ", Finish_Depth = " + JspUtils.sqlEscape(fields[BASE_HORIZON]) + " WHERE Feature_ID = " + featureID);
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException();
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException();
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InvalidCredentialsException();
			}
		}
		return featureID.intValue();
	}

}
