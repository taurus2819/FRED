package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Feature record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class Feature {

	public static final int FEATURE_ID = 0;
	public static final int SITE_ID = 1;
	public static final int AUDIT_ID = 2;
	public static final int SECURITY_CLASS_ID = 20;
	public static final int STATUS = 23;
	public static final int LAST_CHANGE = 25;
	public static final int MASTERFILE_ID = 3;
	public static final int MASTERFILE_NAME = 4;
	public static final int LOCALITY = 5;
	public static final int REG_AREA_ID = 6;
	public static final int COMMENTS = 7;
	public static final int FEATURE_TYPE = 8;
	public static final int FEATURE_NAME = 9;
	public static final int SAMPLE_NAMES = 24;
	public static final int DRILLHOLE_LICENCE_NAME = 10;
	public static final int START_DATE = 11;
	public static final int START_DATE_ROUNDING = 12;
	public static final int FINISH_DATE = 13;
	public static final int FINISH_DATE_ROUNDING = 14;
	public static final int PERSON_ID = 15;
	public static final int DATUM_TYPE = 16;
	public static final int DATUM_ELEVATION = 17;
	public static final int START_DEPTH = 18;
	public static final int FINISH_DEPTH = 19;
	public static final int SAMPLES = 21;
	public static final int PETWELL_LINK = 22;
	public static final int WORKING_FOLDER_ID = 26;

	public static final String OUTCROP_LOCALITY = "Outcrop";
	public static final String DRILLHOLE_LOCALITY = "Drillhole";
	public static final String VERTICAL_SECTION_LOCALITY = "Vertical Section";
	
	private FeatureData fd;
	private PageState state;
	private User user;
	private boolean authenticated;

	public Feature(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.state = state;
		this.user = user;
		this.fd = FeatureData.getData(id, state, forceRefresh);
		if (!FREDUtils.isAllowedLocality(user, fd.getAsString(SECURITY_CLASS_ID), fd.getAsString(STATUS), fd.getAsString(FEATURE_ID), state)) {
			authenticated = false;
		} else {
			authenticated = true;
		}		
	}

	public Feature(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getFeatureID() {
		return fd.getAsInt(FEATURE_ID);
	}

	public String getFeatureType() {
		return fd.getAsString(FEATURE_TYPE);
	}

	public boolean isUserAuthenticated() {
		return authenticated;
	}
	
	public boolean isApprovedLocality() {
		return (fd.getAsString(STATUS).equals("approved"));
	}

	public int getSampleCount() {
		return fd.getAsVector(SAMPLES).size();
	}

	private boolean isAllowedField(int field) {
		if (authenticated) {
			return true;
		}
		if (isApprovedLocality()) {
			switch (field) {
				case FEATURE_ID :
				case FEATURE_TYPE :
				case FEATURE_NAME :
				case SAMPLE_NAMES :
				case MASTERFILE_ID :
				case MASTERFILE_NAME :
				case AUDIT_ID :
				case SECURITY_CLASS_ID :
				case STATUS :
				case SITE_ID :
				case SAMPLES :
				case PETWELL_LINK :
				return true;
			}		
		}
		return false;
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return FeatureData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		FeatureData.purge();
	}

	public String toString() {
		return fd.toString();
	}

	public int addNewSample(String topDepth, String bottomDepth, String drillTypeID, String workingFolderID) throws SQLException, IOException, DataInputException {
		int sampleID;
		if (!FREDUtils.isNumeric(topDepth) || (bottomDepth != null && !bottomDepth.equals("") && !FREDUtils.isNumeric(bottomDepth)) || (drillTypeID != null && !drillTypeID.equals("") && !FREDUtils.isNumeric(drillTypeID))) {
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		}
		DBConnection conn = FREDUtils.getFREDConnection(state);
		//check existing samples.  If there is only one - the default one - then delete it
		ResultSet rs = conn.executeQuery("SELECT S.Sample_ID FROM Sample_All_View S, Record R WHERE S.Sample_ID = R.Sample_ID(+) AND S.Feature_ID = " + getFeatureID() + " AND S.Drillhole_Depth = 'Depth Not Specified' AND S.Collector IS NULL AND R.Sample_ID IS NULL");
		if (rs.next())
			conn.executeUpdate("DELETE FROM Sample WHERE Sample_ID = " + rs.getString(1));
		
		rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
		rs.next();
		String auditID = rs.getString(1);
		conn.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + user.getPersonId() + ", SYSDATE, " + workingFolderID + ")");
		rs = conn.executeQuery("SELECT Sample_Seq.NEXTVAL FROM DUAL");
		rs.next();
		sampleID = rs.getInt(1);
		rs = conn.executeQuery("SELECT MIN(FR_ID) FROM Sample WHERE Feature_ID = " + getFeatureID());
		rs.next();
		conn.executeUpdate("INSERT INTO Sample (Sample_ID, Feature_ID, Audit_ID, FR_ID, Top_Depth, Bottom_Depth, Drill_Type_ID) VALUES (" + sampleID + ", " + getFeatureID() + ", " + auditID + ", " + rs.getString(1) + ", " + JspUtils.sqlEscape(topDepth) + ", " + JspUtils.sqlEscape(bottomDepth) + ", " + JspUtils.sqlEscape(drillTypeID) + ")");
		fd = FeatureData.getData(getFeatureID(), state, true);
		return sampleID;
	}

}
