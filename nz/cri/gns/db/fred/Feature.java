package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents an Audit record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class Feature implements FREDConstants {

	public static final int FEATURE_ID = 0;
	public static final int SITE_ID = 1;
	public static final int AUDIT_ID = 2;
	public static final int MASTERFILE_ID = 3;
	public static final int FIELD_NUMBER = 4;
	public static final int LOCALITY = 5;
	public static final int REG_AREA_ID = 6;
	public static final int COMMENTS = 7;
	public static final int FEATURE_TYPE = 8;
	public static final int FEATURE_NAME = 9;
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

	private PageState state;
	private static Pool featurePool = new Pool();
	private int id;
	private Object[] values = new Object[20];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected Feature(int id, PageState state)
		throws SQLException, IOException {
		this.state = state;
		DBConnection conn =
			ExternalUtils.createDatabaseConnection(
				state.getSession(),
				CONNECTION,
				DB_NAME,
				state.getContext());
		this.id = id;
		featurePool.add(this);
		String query =
			"SELECT FEATURE_ID, SITE_ID, AUDIT_ID, MASTERFILE_ID, FIELD_NUMBER, LOCALITY, REG_AREA_ID, COMMENTS, "
				+ "FEATURE_TYPE, FEATURE_NAME, DRILLHOLE_LICENCE_NAME, START_DATE, START_DATE_ROUNDING, FINISH_DATE, "
				+ "FINISH_DATE_ROUNDING, PERSON_ID, DATUM_TYPE, DATUM_ELEVATION, START_DEPTH, FINISH_DEPTH "
				+ "FROM Feature WHERE Feature_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = ((rs.getString(2) != null) ? new Integer(rs.getInt(2)) : null);
			values[2] =
				((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[3] = ((rs.getString(4) != null) ? new Integer(rs.getInt(4)) : null);
			values[4] = rs.getString(5);
			values[5] = rs.getString(6);
			values[6] = ((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[7] = rs.getString(8);
			values[8] = rs.getString(9);
			values[9] = rs.getDate(10);
			values[10] = rs.getString(11);
			values[11] = rs.getDate(12);
			values[12] = rs.getString(13);
			values[13] = rs.getDate(14);
			values[14] = rs.getString(15);
			values[15] = ((rs.getString(16) != null) ? new Integer(rs.getInt(16)) : null);
			values[16] = rs.getString(17);
			values[17] = ((rs.getString(18) != null) ? new Double(rs.getDouble(18)) : null);
			values[18] = ((rs.getString(19) != null) ? new Double(rs.getDouble(19)) : null);
			values[19] = ((rs.getString(20) != null) ? new Double(rs.getDouble(20)) : null);
			rs.close();
			conn.releaseStatement();
		} catch (SQLException _e) {
			featurePool.removeMe(this);
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
	public static class FeatureFinder implements Finder {
		int id;
		public FeatureFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (
				o instanceof Feature
					&& ((Feature) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return featurePool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		featurePool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Feature getFeature(int id, PageState state)
		throws SQLException, IOException {

		Feature f =
			(Feature) featurePool.retrieve(new FeatureFinder(id));
		if (f != null) {
			return f;
		} else {
			return new Feature(id, state);
		}
	}

	public String toString() {
		return (values[FEATURE_NAME]).toString();
	}

}
