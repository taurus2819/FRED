package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class Record {
	
	//common fields
	public static final int RECORD_ID = 0;
	public static final int FEATURE_ID = 1;
	public static final int SAMPLE_ID = 2;
	public static final int FEATURE_STATUS = 3;
	public static final int SAMPLE_STATUS = 4;
	public static final int SAMPLE_SECURITY_CLASS_ID = 5;
	public static final int FEATURE_NAME = 6;
	public static final int SAMPLE_NAME = 7;
	public static final int AUDIT_ID = 8;
	public static final int STATUS = 9;
	public static final int LAST_CHANGE = 10;
	public static final int WORKING_FOLDER_ID = 11;
	public static final int WORKING_COMMENTS = 12;
	public static final int SECURITY_CLASS_ID = 13;
	public static final int RECORD_TYPE = 14;
	public static final int RECORD_NAME = 15;

	public static final int PERSON = 16;
	public static final int RECORD_DATE = 17;
	public static final int DATE_ROUNDING = 18;
	
	//Adoption record
	public static final int ADOPTOR = 16;
	public static final int ADOPTION_DATE = 17;
	public static final int ADOPTION_DATE_ROUNDING = 18;
	public static final int ADOPTED_STAGE_ID = 19;
	public static final int ADOPTED_STAGE = 20;
	public static final int ADOPTED_STAGE_ABBREV = 21;
	public static final int ADOPTED_STAGE_LOWER_ID = 22;
	public static final int ADOPTED_STAGE_LOWER = 23;
	public static final int ADOPTED_STAGE_LOWER_MOD = 24;
	public static final int ADOPTED_STAGE_UPPER_ID = 25;
	public static final int ADOPTED_STAGE_UPPER = 26;
	public static final int ADOPTED_STAGE_UPPER_MOD = 27;
	public static final int ADOPTED_AGE_START = 28;
	public static final int ADOPTED_AGE_STOP = 29;
	public static final int COMMENTS = 30;

	//Paleontology Record
	public static final int IDENTIFIER = 16;
	public static final int IDENTIFICATION_DATE = 17;
	public static final int IDENTIFICATION_DATE_ROUNDING = 18;
	public static final int STAGE_ID = 19;
	public static final int STAGE = 20;
	public static final int STAGE_ABBREV = 21;
	public static final int STAGE_LOWER_ID = 22;
	public static final int STAGE_LOWER = 23;
	public static final int STAGE_LOWER_MOD = 24;
	public static final int STAGE_UPPER_ID = 25;
	public static final int STAGE_UPPER = 26;
	public static final int STAGE_UPPER_MOD = 27;
	public static final int AGE_START = 28;
	public static final int AGE_STOP = 29;
	public static final int STAGE_COMMENTS = 30;
	public static final int LAB_SECTION_ID = 31;
	public static final int LAB = 32;
	public static final int LAB_CODE = 33;
	public static final int LAB_NUMBER = 34;
	public static final int COLLECTION_COMMENTS = 35;
	public static final int TAXONOMIC_LIST = 36;
	public static final int PROVISIONAL_TAXA_COUNT = 37;
	
	public static final String ADOPTION_RECORD = "ADO";
	public static final String PALEONTOLOGY_RECORD = "PAL";	
	
	private static Pool pool = new Pool();
	protected int id;
	protected Object[] values = new Object[38];
	protected int[] types = { Types.NUMERIC };
	protected Object[] data = new Object[1];
	
	protected Record() {
	}
	
	/**
	 * Cannot be called directly. use static getData method instead.
	 */
	protected Record(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, SAMPLE_STATUS, "
				+ "SAMPLE_SECURITY_CLASS_ID, FEATURE_NAME, SAMPLE_NAME, AUDIT_ID, "
				+ "STATUS, LAST_CHANGE, WORKING_FOLDER_ID, WORKING_COMMENTS, "
				+ "SECURITY_CLASS_ID, RECORD_TYPE, RECORD_NAME "
				+ "FROM Record_All_View WHERE Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[RECORD_ID] = new Integer(rs.getInt(1));
			values[FEATURE_ID] = new Integer(rs.getInt(2));
			values[SAMPLE_ID] = new Integer(rs.getInt(3));
			values[FEATURE_STATUS] = rs.getString(4);
			values[SAMPLE_STATUS] = rs.getString(5);
			values[SAMPLE_SECURITY_CLASS_ID] = ((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[FEATURE_NAME] = rs.getString(7);
			values[SAMPLE_NAME] = rs.getString(8);
			values[AUDIT_ID] = ((rs.getString(9) != null) ? new Integer(rs.getInt(9)) : null);
			values[STATUS] = rs.getString(10);
			values[LAST_CHANGE] = rs.getDate(11);
			values[WORKING_FOLDER_ID] = ((rs.getString(12) != null) ? new Integer(rs.getInt(12)) : null);
			values[WORKING_COMMENTS] = rs.getString(13);
			values[SECURITY_CLASS_ID] = ((rs.getString(14) != null) ? new Integer(rs.getInt(14)) : null);
			values[RECORD_TYPE] = rs.getString(15);
			values[RECORD_NAME] = rs.getString(16);
			rs.close();
			conn.releaseStatement();
		} catch (SQLException _e) {
			pool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
	}
	
	public int getRecordID() {
		return getAsInt(RECORD_ID);
	}
	
	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return ((Integer) thing).intValue();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return ((Double) thing).doubleValue();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (java.util.Date) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			if (thing == null) {
				return null;
			}
			return thing.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException("FieldID = " + String.valueOf(field));
		}
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (Vector) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return thing;
		}
		catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Inner class used for object pooling.
	 */
	public static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (
				o instanceof Record
					&& ((Record) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		pool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. If forceRefresh = true 
	 * @throws SQLException if there is not a record for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException, InvalidCredentialsException {
		Record rec = (Record) pool.retrieve(new DataFinder(id));
		if (forceRefresh && rec != null) {
			pool.removeMe(rec);
			rec = null;
		}
		if (rec == null)
			rec = new Record(id, state);
		if (!FREDUtils.isAllowedLocality(user, rec.getAsString(FEATURE_STATUS), rec.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedSample(user, rec.getAsString(SAMPLE_SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(SAMPLE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, rec.getAsString(SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(RECORD_ID), state))
			throw new InvalidCredentialsException();
		return rec;
	}

	/**
	 *  Use this to get a new instance of this class. If record already exists in the pool then it is retrieved
	 * @throws SQLException if there is not a record for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		return getData(id, user, state, false);
	}

	public String toString() {
		String person = ((values[PERSON] != null) ? ((KeyValueObject) getAsVector(PERSON).firstElement()).getValue() : "");
		String date = ((values[RECORD_DATE] != null) ? FREDUtils.formatDateForOutput(getAsDate(RECORD_DATE), getAsString(DATE_ROUNDING)) : "");
		return ((person.length() + date.length() > 0) ? "(" + person + ((person.length() > 0 && date.length() > 0) ? ", " : "") + date + ")" : "");
	}
	
}
