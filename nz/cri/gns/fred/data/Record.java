package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class Record {
	
	//common fields
	public static final int RECORD_ID = 0;
	public static final int RECORD_TYPE = 80;
	public static final int FEATURE_ID = 1;
	public static final int SAMPLE_ID = 2;
	public static final int FEATURE_STATUS = 3;
	public static final int FEATURE_SECURITY_CLASS_ID = 4;
	public static final int AUDIT_ID = 5;
	public static final int STATUS = 6;
	public static final int SECURITY_CLASS_ID = 7;
	public static final int SAMPLE_NAME = 8;
	public static final int DRILLHOLE_DEPTH = 9;
	public static final int LAST_CHANGE = 81;
	public static final int WORKING_FOLDER_ID = 82;
	public static final int WORKING_COMMENTS = 83;
	public static final int RECORD_NAME = 84;

	protected static Pool pool = new Pool();
	protected int id;
	protected Object[] values = new Object[85];
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
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, FEATURE_SECURITY_CLASS_ID, AUDIT_ID, "
				+ "STATUS, SECURITY_CLASS_ID, SAMPLE_NAME, DRILLHOLE_DEPTH, RECORD_TYPE, LAST_CHANGE, "
				+ "WORKING_FOLDER_ID, WORKING_COMMENTS, RECORD_NAME "
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
			values[FEATURE_SECURITY_CLASS_ID] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[AUDIT_ID] =
				((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[STATUS] = rs.getString(7);
			values[SECURITY_CLASS_ID] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[SAMPLE_NAME] = rs.getString(9);
			values[DRILLHOLE_DEPTH] = rs.getString(10);
			values[RECORD_TYPE] = rs.getString(11);
			values[LAST_CHANGE] = rs.getDate(12);
			values[WORKING_FOLDER_ID] = ((rs.getString(13) != null) ? new Integer(rs.getInt(13)) : null);
			values[WORKING_COMMENTS] = rs.getString(14);
			values[RECORD_NAME] = rs.getString(15);
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
			throw new IllegalArgumentException();
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
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state) throws SQLException, IOException, AccessDeniedException {
		Record rec = (Record) pool.retrieve(new DataFinder(id));
		if (rec == null) {
			rec = new Record(id, state);
		}
		if (!FREDUtils.isAllowedLocality(user, rec.getAsString(FEATURE_SECURITY_CLASS_ID), rec.getAsString(FEATURE_STATUS), rec.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, rec.getAsString(SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(RECORD_ID), state)) {
			throw new AccessDeniedException();
		}
		return rec;
	}

	public String toString() {
		return (values[0]).toString();
	}
	
}
