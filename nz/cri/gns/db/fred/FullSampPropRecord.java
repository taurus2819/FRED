package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.jsp.PageState;

import nz.cri.gns.jsp.FREDConstants;

/**
 * Class that represents a Sample_Property_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampleView method instead.
 */
public class FullSampPropRecord {

	public static final int RECORD_ID = 0;
	public static final int FEATURE_ID = 1;
	public static final int SAMPLE_ID = 2;
	public static final int FEATURE_STATUS = 3;
	public static final int FEATURE_SECURITY_CLASS_ID = 4;
	public static final int AUDIT_ID = 5;
	public static final int STATUS = 6;
	public static final int SECURITY_CLASS_ID = 7;
	public static final int SAMPLE_NAME = 8;
	public static final int DRILLHOLE_DEPTH = 9;
	public static final int COLLECTOR_ID = 10;
	public static final int COLLECTOR = 11;
	public static final int COLLECTION_DATE = 12;
	public static final int DATE_ROUNDING = 13;

	protected static Pool fullSampPropPool = new Pool();
	protected int id;
	private Object[] values = new Object[14];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected FullSampPropRecord(int id, PageState state) throws SQLException, IOException {
		DBConnection conn = conn =
		ExternalUtils.createDatabaseConnection(state.getSession(), FREDConstants.CONNECTION, FREDConstants.DB_NAME, state.getContext());
		this.id = id;
		fullSampPropPool.add(this);
		String query =
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, FEATURE_SECURITY_CLASS_ID, AUDIT_ID, "
				+ "STATUS, SECURITY_CLASS_ID, SAMPLE_NAME, DRILLHOLE_DEPTH, COLLECTION_DATE, DATE_ROUNDING "
				+ "FROM Sample_Property_All_View WHERE Status = 'approved' AND Record_ID = "
				+ this.id;
		try {
			ResultSet rs = conn.executeQuery(query);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = new Integer(rs.getInt(2));
			values[2] = new Integer(rs.getInt(3));
			values[3] = rs.getString(4);
			values[4] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[5] = ((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[6] = rs.getString(7);
			values[7] = ((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getString(10);
			values[12] = rs.getDate(11);
			values[13] = rs.getString(12);
			rs.close();
			query = "SELECT Person_ID, Name FROM Person_View NATURAL JOIN Collector WHERE Record_ID = " + this.id;
			rs = conn.executeQuery(query);
			Vector collID = new Vector();
			Vector coll = new Vector();
			while (rs.next()) {
				collID.add(new Integer(rs.getInt(1)));
				coll.add(rs.getString(2));
			}
			rs.close();
			values[10] = collID;
			values[11] = coll;
			conn.releaseStatement();
		} catch (SQLException _e) {
			fullSampPropPool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		try {
			return ((Integer) values[field]).intValue();
		} catch (Exception _e) {
			throw new IllegalArgumentException("Field cannot be returned as an int");
		}
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		try {
			return ((Double) values[field]).doubleValue();
		} catch (Exception _e) {
			throw new IllegalArgumentException("Field cannot be returned as an double");
		}
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		Object thing = values[field];
		try {
			return (java.util.Date) thing;
		} catch (Exception _e) {
			throw new IllegalArgumentException(
				"Field cannot be returned as a Date, class is "
					+ thing.getClass().getName());
		}
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		Object thing = values[field];
		try {
			return (Vector) thing;
		} catch (Exception _e) {
			throw new IllegalArgumentException(
				"Field cannot be returned as a Vector, class is "
					+ thing.getClass().getName());
		}
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		if (values[field] == null)
			return null;
		return values[field].toString();
	}

	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		return values[field];
	}

	/**
	 * Inner class used for object pooling.
	 */
	public static class FullSampPropFinder implements Finder {
		int id;
		public FullSampPropFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof FullSampPropRecord && ((FullSampPropRecord) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return fullSampPropPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		fullSampPropPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 * @throws AccessDeniedException where user not allowed access to this row
	 */
	public static FullSampPropRecord getFullSampPropRecord(int id, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException {
		FullSampPropRecord f =
			(FullSampPropRecord) fullSampPropPool.retrieve(new FullSampPropFinder(id));
		if (f == null) {
			f = new FullSampPropRecord(id, state);
		}
		if (!FREDUtils.isAllowedToView(user, f.getAsInt(FEATURE_SECURITY_CLASS_ID), state) || !FREDUtils.isAllowedToView(user, f.getAsInt(SECURITY_CLASS_ID), state)) {
			throw new AccessDeniedException();
		}
		return f;
	}

	public String toString() {
		return (values[RECORD_ID]).toString();
	}

}