package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 */
public class Sample {

	public static final int FEATURE_ID = 0;
	public static final int SAMPLE_ID = 1;
	public static final int FEATURE_TYPE = 2;
	public static final int SAMPLE_NAME = 3;
	public static final int FR_ID = 4;
	public static final int FR_NUMBER = 5;
	public static final int YARD_FR_ID = 6;
	public static final int YARD_FR_NUMBER = 7;
	public static final int FEATURE_NAME = 8;
	public static final int MAP_SHEET = 9;
	public static final int SERIAL_NUMBER = 10;
	public static final int RECOLLECTION_NUMBER = 11;
	public static final int YARD_MAP_SHEET = 12;
	public static final int YARD_SERIAL_NUMBER = 13;
	public static final int YARD_RECOLLECTION_NUMBER = 14;
	public static final int DRILLHOLE_DEPTH = 15;
	public static final int TOP_DEPTH = 16;
	public static final int BOTTOM_DEPTH = 17;
	public static final int DRILL_TYPE_ID = 61;
	public static final int DRILL_TYPE = 18;
	public static final int MASTERFILE_ID = 19;
	public static final int MASTERFILE_NAME = 20;
	public static final int REG_AREA_ID = 21;
	public static final int REG_AREA_NAME = 22;
	public static final int REG_AREA_CODE = 60;
	public static final int AUDIT_ID = 23;
	public static final int STATUS = 24;
	public static final int LAST_CHANGE = 25;
	public static final int WORKING_FOLDER_ID = 26;
	public static final int WORKING_COMMENTS = 27;
	public static final int SECURITY_CLASS_ID = 28;
	public static final int SITE_ID = 29;
	public static final int COUNTRY_CODE = 56;
	public static final int COUNTRY_NAME = 57;
	public static final int LATITUDE = 30;
	public static final int LONGITUDE = 31;
	public static final int QMAP_SHEET = 32;
	public static final int NZMG_SHEET = 33;
	public static final int NZMG_EAST = 34;
	public static final int NZMG_NORTH = 35;
	public static final int METHOD_ID = 52;
	public static final int METHOD = 36;
	public static final int ACCURACY = 37;
	public static final int ORIG_SYSTEM_ID = 53;
	public static final int COORD_SYSTEM = 54;
	public static final int ORIG_COORD = 55;
	public static final int LOCALITY = 38;
	public static final int DRILLHOLE_LICENCE_NAME = 39;
	public static final int PERSON_ID = 40;
	public static final int PERSON = 41;
	public static final int START_DATE = 42;
	public static final int START_DATE_ROUNDING = 43;
	public static final int FINISH_DATE = 44;
	public static final int FINISH_DATE_ROUNDING = 45;
	public static final int DATUM_TYPE = 46;
	public static final int DATUM_ELEVATION = 47;
	public static final int START_DEPTH = 48;
	public static final int FINISH_DEPTH = 49;
	public static final int RECORDS = 50;
	public static final int WORKING_RECORDS = 51;
	public static final int SAMPLE_PROPERTY_RECORD_ID = 58;
	public static final int SAMPLE_PROPERTY_RECORD_STATUS = 59;

	private SampleData sd;
	private PageState state;
	private boolean authenticated = false;

	public Sample(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.sd = SampleData.getData(id, state, forceRefresh);
		this.state = state;
		if (!FREDUtils.isAllowedLocality(user, sd.getAsString(SECURITY_CLASS_ID), sd.getAsString(STATUS), sd.getAsString(FEATURE_ID), state)) {
			authenticated = false;
		} else {
			authenticated = true;
		}		
	}

	public Sample(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getSampleID() {
		return sd.getAsInt(SAMPLE_ID);
	}

	public boolean isUserAuthenticated() {
		return authenticated;
	}

	public boolean isApprovedLocality() {
		return (sd.getAsString(STATUS).equals("approved"));
	}

	public int getRecordCount() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
		return sd.getAsVector(RECORDS).size();
	}

	public int getWorkingRecordCount() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
		return sd.getAsVector(WORKING_RECORDS).size();
	}

	public int getAdoptionRecordCount() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
		int adoCount = 0;
		for (Iterator i = sd.getAsVector(RECORDS).iterator(); i.hasNext(); ) {
			KeyValueObject rec = (KeyValueObject)i.next();
			if (rec.getValue().equals("ADO"))
				adoCount++;
		}
		return adoCount;		
	}

	public int getPaleontologyRecordCount() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
		int palCount = 0;
		for (Iterator i = sd.getAsVector(RECORDS).iterator(); i.hasNext(); ) {
			KeyValueObject rec = (KeyValueObject)i.next();
			if (rec.getValue().equals("PAL"))
				palCount++;
		}
		return palCount;		
	}
	
	private boolean isAllowedField(int field) {
		if (authenticated) {
			return true;
		}
		switch (field) {
			case FEATURE_ID :
			case SAMPLE_ID :
			case FEATURE_TYPE :
			case SAMPLE_NAME :
			case FR_ID:
			case FR_NUMBER:
			case YARD_FR_ID:
			case YARD_FR_NUMBER:
			case FEATURE_NAME:
			case MAP_SHEET:
			case SERIAL_NUMBER:
			case RECOLLECTION_NUMBER:
			case YARD_MAP_SHEET:
			case YARD_SERIAL_NUMBER:
			case YARD_RECOLLECTION_NUMBER:
			case DRILLHOLE_DEPTH:
			case MASTERFILE_ID:
			case MASTERFILE_NAME:
			case REG_AREA_CODE:
			case AUDIT_ID:
			case SECURITY_CLASS_ID:
			case STATUS:
			case SITE_ID:
			case COUNTRY_CODE:
			case COUNTRY_NAME:
			case LATITUDE:
			case LONGITUDE:
			case QMAP_SHEET:
			case NZMG_SHEET:
			case NZMG_EAST:
			case NZMG_NORTH:
			case METHOD_ID:
			case METHOD:
			case ACCURACY:
			case ORIG_SYSTEM_ID:
			case COORD_SYSTEM:
			case ORIG_COORD:
			return true;
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
		return sd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return sd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return sd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return sd.getAsVector(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return sd.getAsString(field);
	}

	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return sd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return SampleData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		SampleData.purge();
	}

	public String toString() {
		return sd.toString();
	}

	public void editSample(String topDepth, String bottomDepth, String drillTypeID) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		conn.executeUpdate("UPDATE Sample SET Top_Depth = " + JspUtils.sqlEscape(topDepth) + ", Bottom_Depth = " + JspUtils.sqlEscape(bottomDepth) + ", Drill_Type_ID = " + JspUtils.sqlEscape(drillTypeID) + " WHERE Sample_ID = " + getSampleID());
	}

}