package nz.cri.gns.db.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Record_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class Record {

	public static final int RECORD_ID = 0;
	public static final int RECORD_TYPE = 1;
	public static final int RECORD_NAME = 2;
	public static final int FEATURE_ID = 3;
	public static final int SAMPLE_ID = 4;
	public static final int FEATURE_STATUS = 5;
	public static final int FEATURE_SECURITY_CLASS_ID = 6;
	public static final int AUDIT_ID = 7;
	public static final int STATUS = 8;
	public static final int LAST_CHANGE = 9;
	public static final int SECURITY_CLASS_ID = 10;
	public static final int SAMPLE_NAME = 11;
	public static final int DRILLHOLE_DEPTH = 12;
	public static final int WORKING_FOLDER_ID = 13;

	protected static Pool pool = new Pool();
	protected int id;
	private Object[] values = new Object[14];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getAdoptionRecord method instead.
	 */
	protected Record(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT RECORD_ID, RECORD_TYPE, RECORD_NAME, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, "
				+ "FEATURE_SECURITY_CLASS_ID, AUDIT_ID, STATUS, LAST_CHANGE, SECURITY_CLASS_ID, "
				+ "SAMPLE_NAME, DRILLHOLE_DEPTH, WORKING_FOLDER_ID "
				+ "FROM Record_All_View WHERE Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = rs.getString(2);
			values[2] = rs.getString(3);
			values[3] = new Integer(rs.getInt(4));
			values[4] = new Integer(rs.getInt(5));
			values[5] = rs.getString(6);
			values[6] =
				((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[7] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getDate(10);
			values[10] =
				((rs.getString(11) != null)
					? new Integer(rs.getInt(11))
					: null);
			values[11] = rs.getString(12);
			values[12] = rs.getString(13);
			values[13] = ((rs.getString(14) != null) ? new Integer(rs.getInt(14)) : null);
			conn.releaseStatement();
		} catch (SQLException _e) {
			pool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
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
	public java.util.Date getAsDate(int field)
		throws IllegalArgumentException {
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
		} catch (Exception e) {
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
			return (o instanceof Record && ((Record) o).id == this.id);
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
	public static Record getData(int id, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException {
		Record r = (Record) pool.retrieve(new DataFinder(id));
		if (r == null) {
			r = new Record(id, state);
		}
		if (!FREDUtils
			.isAllowedLocality(
				user,
				r.getAsString(FEATURE_SECURITY_CLASS_ID),
				r.getAsString(FEATURE_STATUS),
				r.getAsString(FEATURE_ID),
				state)
			|| !FREDUtils.isAllowedRecord(
				user,
				r.getAsString(SECURITY_CLASS_ID),
				r.getAsString(STATUS),
				r.getAsString(RECORD_ID),
				state)) {
			throw new AccessDeniedException();
		}
		return r;
	}

	public String toString() {
		return (values[0]).toString();
	}

	//public void finalize() throws Throwable {
	//	pool.removeMe(this);
	//}

}
