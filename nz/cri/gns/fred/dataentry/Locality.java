package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;

public abstract class Locality {

	public static final int FEATURE_NAME = 0;
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
	public static final int OPERATING_COMPANY = 8;
	public static final int SPUD_DATE = 9;
	public static final int COMPLETION_DATE = 10;
	public static final int LICENCE_AREA = 11;
	public static final int DATUM_TYPE = 12;
	public static final int DATUM_ELEVATION = 13;
	public static final int KICK_OFF_DEPTH = 14;
	public static final int TERMINATION_DEPTH = 15;

	protected User user;
	protected PageState state;
	protected Folder folder;
	protected Integer featureID;
	protected Integer sampleID;
	protected String featureType;
	protected Feature feature;
	protected Sample sample;
	protected String[] fields = new String[20];
	private Double latitude, longitude;
	private String origSystemID, origCoord, countryCode, recoll;
	protected boolean savedFlag = false;


	public Locality(User user, int folderID, String featureType, PageState state) throws SQLException, IOException, DataInputException {
		this.user = user;
		this.state = state;
		if (!(featureType.equals("Outcrop") || featureType.equals("Drillhole") || featureType.equals("VertSect"))) throw new DataInputException("Feature Type", "Invalid value");
		this.featureType = featureType;
		this.folder = new Folder(folderID, user, state);
	}

	public Locality(int id, User user, PageState state) throws IOException,	SQLException, DataInputException, InvalidCredentialsException {
		this.user = user;
		this.state = state;
		featureID = new Integer(id);
		feature = new Feature(id, user, state);
		sampleID = (Integer) feature.getAsVector(Feature.SAMPLES).firstElement();
		sample = new Sample(sampleID.intValue(), user, state);
		featureType = sample.getAsString(Sample.FEATURE_TYPE);
		if (sample.get(Sample.WORKING_FOLDER_ID) != null) this.folder = new Folder(sample.getAsInt(Sample.WORKING_FOLDER_ID), user, state);
		setField(FEATURE_NAME, sample.getAsString(Sample.FEATURE_NAME));
		setField(REGISTRATION_AREA, sample.getAsString(Sample.REG_AREA_ID));
		String workComm = sample.getAsString(Sample.WORKING_COMMENTS);
		if (workComm != null && workComm.indexOf("*Recoll:") >= 0) {
			setField(RECOLLECTION, workComm.substring(8, workComm.indexOf("*", 2)).trim());
			setField(WORKING_COMMENTS, workComm.substring(workComm.indexOf("*", 2) + 1, workComm.length()).trim());
		} else {
			setField(WORKING_COMMENTS, workComm);
		}	
		if (sample.get(Sample.ORIG_SYSTEM_ID) != null) {
			int origSystemID = sample.getAsInt(Sample.ORIG_SYSTEM_ID);
			if (origSystemID == 38) {
				setField(GRID_REF,	"NZMG:"	+ sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
			} else if (origSystemID == 16) {
				setField(GRID_REF,	"TruncNZMG:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
			} else if (origSystemID == 29) {
				setField(GRID_REF,	"LatLong:"	+ sample.getAsString(Sample.COUNTRY_CODE) + "*" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
			}
		}
		setField(METHOD, sample.getAsString(Sample.METHOD_ID));
		setField(ACCURACY, sample.getAsString(Sample.ACCURACY));
		setField(LOCALITY_DESC, sample.getAsString(Sample.LOCALITY));
		savedFlag = true;
	}

	public Integer getFeatureID() {
		return featureID;
	}

	public Integer getSampleID() {
		return sampleID;
	}

	public void setField(int field, String value) throws DataInputException {
		if (value != null)
			parseField(field, value);
		fields[field] = value;
		savedFlag = false;
	}

	public String getField(int field) {
		return fields[field];
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case REGISTRATION_AREA :
					rs = 
						conn.executeQuery(
							"SELECT * FROM Lookup WHERE Lookup_ID = "
								+ value
								+ " AND FieldName = 'RegArea'");
					if (!rs.next())
						throw new DataInputException(
							"Registration Area",
							"Invalid value");
					break;
				case GRID_REF :
					if (!parseCoord(value))
						throw new DataInputException(
							"Coordinate",
							"Invalid value");
					break;
				case METHOD :
					rs =
						conn.executeQuery(
							"SELECT * FROM SC.Method WHERE Method_ID = "
								+ value);
					if (!rs.next())
						throw new DataInputException(
							"Coordinate Method",
							"Invalid value");
					break;
				case ACCURACY :
					try {
						Double acc = new Double(value);
					} catch (Exception e) {
						throw new DataInputException(
							"Accuracy",
							"Invalid value");
					}
					break;
				case RECOLLECTION :
					recoll = "*Recoll:" + value + "*";
					rs = conn.executeQuery("SELECT * FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(value) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + user.getPersonId() + "))");
					if (!rs.next()) {
						throw new DataInputException("Recollection/Sidetrack", value + " is not an existing FR Number or temporary name.  Please use the builder to select.");
					}
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();	
		}
	}

	private boolean parseCoord(String coord) {
		if (coord.indexOf("*") == -1
			|| coord.indexOf("*") == coord.length() - 1)
			return false;
		if (coord.indexOf("NZMG:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north =
				coord.substring(coord.indexOf("*") + 1, coord.length());
			if (east.length() != 7 || north.length() != 7)
				return false;
			try {
				NorthingEasting nzmgCoord =
					new NorthingEasting(
						Double.parseDouble(north),
						Double.parseDouble(east));
				NZMG nzmg = new NZMG();
				Datum.LatLong latLong = nzmg.convertToNZGD49(nzmgCoord);
				latitude = new Double(latLong.getNorthSouth());
				longitude = new Double(latLong.getEastWest());
				origCoord = east + "|" + north;
				origSystemID = "38";
				countryCode = "NZ";
			} catch (Exception e) {
				return false;
			}
		} else if (coord.indexOf("TruncNZMG:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				return false;
			String sheet = coord.substring(10, coord.indexOf("*"));
			String east =
				coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String north =
				coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			if (sheet.length() != 3
				|| east.length() < 3
				|| east.length() > 4
				|| north.length() < 3
				|| north.length() > 4
				|| east.length() != north.length())
				return false;
			try {
				TruncNorthingEasting truncNzmgCoord =
					new TruncNorthingEasting(
						Double.parseDouble(north),
						Double.parseDouble(east),
						sheet,
						east.length());
				NZMS260 nzms260 = new NZMS260();
				Datum.LatLong latLong = nzms260.convertToNZGD49(truncNzmgCoord);
				latitude = new Double(latLong.getNorthSouth());
				longitude = new Double(latLong.getEastWest());
				origCoord = sheet + "|" + east + "|" + north;
				origSystemID ="16";
				countryCode = "NZ";
			} catch (Exception e) {
				return false;
			}
		} else if (coord.indexOf("LatLong:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				return false;
			String north =
				coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String east =
				coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				latitude = new Double(north);
				longitude = new Double(east);
				if (longitude.doubleValue() <= -180
					|| longitude.doubleValue() > 180
					|| latitude.doubleValue() <= -90
					|| latitude.doubleValue() > 90)
					return false;
				origCoord = latitude + "|" + longitude;
				origSystemID = "29";
				countryCode = coord.substring(8, coord.indexOf("*"));
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public void save() throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			String siteID = null;
			if (fields[GRID_REF] != null) siteID = getSiteID();
			if (featureID == null) {
				if (!folder.isAllowedCreateLocalities()) throw new InvalidCredentialsException();
				//create new AUDIT, FEATURE and SAMPLE records
				rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				String auditID = rs.getString(1);
				conn.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + user.getPersonId() + ", SYSDATE, " + JspUtils.sqlEscape(FREDUtils.noNulls(recoll) + FREDUtils.noNulls(fields[WORKING_COMMENTS])) + ", " + folder.getFolderID() + ")");
				rs = conn.executeQuery("SELECT Feature_Seq.NEXTVAL FROM DUAL");
				rs.next();
				featureID = new Integer(rs.getInt(1));
				conn.executeUpdate("INSERT INTO Feature (Feature_ID, Site_ID, Audit_ID, Feature_Type, Locality, Feature_Name, Reg_Area_ID) VALUES (" + featureID + ", " + JspUtils.sqlEscape(siteID) + ", " + auditID + ", " + JspUtils.sqlEscape(featureType) + ", " + JspUtils.sqlEscape(fields[LOCALITY_DESC]) + ", " + JspUtils.sqlEscape(fields[FEATURE_NAME]) + ", " + JspUtils.sqlEscape(fields[REGISTRATION_AREA]) + ")");
				conn.executeUpdate("INSERT INTO Sample (Feature_ID) VALUES (" + featureID + ")");
			} else { // edit
				if (!folder.isAllowedEditLocalities()) throw new InvalidCredentialsException();
				//Update AUDIT
				rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featureID);
				rs.next();
				String auditID = rs.getString(1);
				conn.executeUpdate("UPDATE Audit_Table SET Modified_By_ID = " + user.getPersonId() + ", Modified_Date = SYSDATE, Working_Comments = " + JspUtils.sqlEscape(FREDUtils.noNulls(recoll) + FREDUtils.noNulls(fields[WORKING_COMMENTS])) + " WHERE Audit_ID = " + auditID);
				conn.executeUpdate("UPDATE Feature SET Site_ID = " + JspUtils.sqlEscape(siteID) + ", Locality = " + JspUtils.sqlEscape(fields[LOCALITY_DESC]) + ", Feature_Name = " + JspUtils.sqlEscape(fields[FEATURE_NAME]) + ", Reg_Area_ID = " + JspUtils.sqlEscape(fields[REGISTRATION_AREA]) + " WHERE Feature_ID = " + featureID);
			}
			conn.releaseStatement();
		}
		savedFlag = true;
	}

	public void submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		if (!folder.isAllowedSubmitLocalities()) throw new InvalidCredentialsException();
		if (featureType == null || fields[GRID_REF] == null || fields[REGISTRATION_AREA] == null) throw new DataInputException("Mandatory Fields", "Not all completed");
		save();
		//change status, check MF & add saved record to folder
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Code FROM Lookup WHERE FieldName = 'RegArea' AND Lookup_ID = " + fields[REGISTRATION_AREA]);
		rs.next();
		String regCode = rs.getString(1);
		rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featureID);
		rs.next();
		String auditID = rs.getString(1);
		rs = conn.executeQuery("SELECT Which_Masterfile(" + JspUtils.sqlEscape(regCode) + ", " + latitude + ", " + longitude + ") FROM DUAL");
		rs.next();
		String mfID = rs.getString(1);
		conn.executeUpdate("UPDATE Feature SET Masterfile_ID = " + mfID + " WHERE Feature_ID = " + featureID);
		conn.executeUpdate("UPDATE Audit_Table SET Status = 'waiting', Submitted_By_ID = " + user.getPersonId() + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + auditID);
		rs = conn.executeQuery("SELECT * FROM Folder_Content_View WHERE Feature_ID = " + featureID + " AND Folder_ID = " + folder.getFolderID());
		if (!rs.next()) {
			conn.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + folder.getFolderID() + ", " + featureID + ")");
		}
		conn.releaseStatement();
	}

	public void revoke() throws SQLException, IOException, InvalidCredentialsException {
		if (!folder.isAllowedSubmitLocalities()) throw new InvalidCredentialsException();
		save();
		//change status, check MF & add saved record to folder
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featureID);
		rs.next();
		String auditID = rs.getString(1);
		conn.executeUpdate("UPDATE Audit_Table SET Status = 'revoked', Working_Folder_ID = " + folder.getFolderID() + ", Working_Comments = NULL WHERE Audit_ID = " + auditID);
		conn.releaseStatement();
	}

	public void delete() throws IOException, SQLException, InvalidCredentialsException {
		if (!folder.isAllowedDeleteLocalities()) throw new InvalidCredentialsException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		StringBuffer auditID = new StringBuffer();
		ResultSet rs = conn.executeQuery("SELECT Audit_ID FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featureID + ")");
		while (rs.next()) {
			auditID.append(rs.getString(1) + ",");
		}
		rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featureID);
		rs.next();
		auditID.append(rs.getString(1));
		conn.executeUpdate("DELETE FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featureID + ")");
		conn.executeUpdate("DELETE FROM Feature WHERE Feature_ID = " + featureID);
		conn.executeUpdate("DELETE FROM Audit_Table WHERE Audit_ID IN (" + auditID + ")");
		conn.releaseStatement();
	}

	protected String parseDate(String dateStr, String dateRnd) throws DataInputException {
		String date, day, month, year;
		if (dateStr.lastIndexOf("/") == dateStr.length() - 1) throw new DataInputException("Date", "Invalid Data"); //ends with slash
		if (dateRnd.equals("Year") && dateStr.indexOf("/") == -1 && dateStr.length() == 4) { //year only
			try {
				date = "1/1/" + Integer.parseInt(dateStr);
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Data");
			}
		} else {
			if (dateStr.indexOf("/") == dateStr.lastIndexOf("/")) {
				day = "1";
				month = dateStr.substring(0, dateStr.indexOf("/"));
				year = dateStr.substring(dateStr.indexOf("/") + 1, dateStr.length());
			} else {
				day = dateStr.substring(0, dateStr.indexOf("/"));
				month = dateStr.substring(dateStr.indexOf("/") + 1, dateStr.lastIndexOf("/"));
				year = dateStr.substring(dateStr.lastIndexOf("/") + 1, dateStr.length());
			}
			try {
				int iDay = Integer.parseInt(day);
				int iMonth = Integer.parseInt(month);
				int iYear = Integer.parseInt(year);
				if (iDay < 0 || iDay > 31) throw new DataInputException("Date", "Invalid Date"); //bad day
				if (iMonth < 0 || iMonth > 12) throw new DataInputException("Date", "Invalid Date"); //bad month
				if (year.length() != 4) throw new DataInputException("Date", "Invalid Date"); //bad year
				if (iMonth == 2 && iDay > 28) throw new DataInputException("Date", "Invalid Date"); //bad Feb
				if ((iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11) && iDay > 30) throw new DataInputException("Date", "Invalid Date"); //bad 30 day months
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Date");
			}
			date = day + "/" + month + "/" + year;
		}
		return date;
	}

	private String getSiteID() throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String siteID;
		ResultSet rs = conn.executeQuery("SELECT SC.Site_Check(" + latitude	+ ", " + longitude + ", " + JspUtils.sqlEscape(fields[METHOD])
					+ ", "
					+ JspUtils.sqlEscape(fields[ACCURACY])
					+ ") FROM DUAL");
		rs.next();
		if (rs.getString(1) != null) {
			siteID = rs.getString(1);
		} else {
			rs = conn.executeQuery("SELECT SC.Site_Seq.NEXTVAL FROM DUAL");
			rs.next();
			siteID = rs.getString(1);
			conn.executeUpdate(
				"INSERT INTO SC.Site (Site_ID, Site_Name, Latitude, Longitude, Method_ID, Accuracy, Directions, Orig_System_ID, Orig_Coord, Country_Code) VALUES ("
					+ siteID
					+ ", "
					+ JspUtils.sqlEscape(fields[FIELD_NUMBER])
					+ ", "
					+ latitude
					+ ", "
					+ longitude
					+ ", "
					+ JspUtils.sqlEscape(fields[METHOD])
					+ ", "
					+ JspUtils.sqlEscape(fields[ACCURACY])
					+ ", "
					+ JspUtils.sqlEscape(fields[LOCALITY_DESC])
					+ ", "
					+ JspUtils.sqlEscape(origSystemID)
					+ ", "
					+ JspUtils.sqlEscape(origCoord)
					+ ", "
					+ JspUtils.sqlEscape(countryCode)
					+ ")");
		}
		conn.releaseStatement();
		return siteID;
	}

}
