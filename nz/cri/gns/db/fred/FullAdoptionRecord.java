package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_Property_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampPropRecord method instead.
 */
public class FullAdoptionRecord {

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
	public static final int ADOPTOR = 10;
	public static final int ADOPTION_DATE = 11;
	public static final int DATE_ROUNDING = 12;
	public static final int ADOPTED_STAGE_ID = 13;
	public static final int ADOPTED_STAGE = 14;
	public static final int ADOPTED_STAGE_ABBREV = 15;
	public static final int ADOPTED_STAGE_LOWER_ID = 16;
	public static final int ADOPTED_STAGE_LOWER = 17;
	public static final int ADOPTED_STAGE_LOWER_MOD = 18;
	public static final int ADOPTED_STAGE_UPPER_ID = 19;
	public static final int ADOPTED_STAGE_UPPER = 20;
	public static final int ADOPTED_STAGE_UPPER_MOD = 21;
	public static final int ADOPTED_AGE_START = 22;
	public static final int ADOPTED_AGE_STOP = 23;
	public static final int COMMENTS = 24;

	protected static Pool fullAdoptionPool = new Pool();
	protected int id;
	private Object[] values = new Object[25];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getAdoptionRecord method instead.
	 */
	protected FullAdoptionRecord(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		fullAdoptionPool.add(this);
		String query =
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, FEATURE_SECURITY_CLASS_ID, AUDIT_ID, "
				+ "STATUS, SECURITY_CLASS_ID, SAMPLE_NAME, DRILLHOLE_DEPTH, ADOPTION_DATE, DATE_ROUNDING, "
				+ "ADOPTED_STAGE_ID, ADOPTED_STAGE, ADOPTED_STAGE_ABBREV, ADOPTED_STAGE_LOWER_ID, "
				+ "ADOPTED_STAGE_LOWER, ADOPTED_STAGE_LOWER_MOD, ADOPTED_STAGE_UPPER_ID, ADOPTED_STAGE_UPPER, "
				+ "ADOPTED_STAGE_UPPER_MOD, ADOPTED_AGE_START, ADOPTED_AGE_STOP, COMMENTS "
				+ "FROM Adoption_All_View WHERE Status = 'approved' AND Feature_Status = 'approved' AND Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
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
			values[5] =
				((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[6] = rs.getString(7);
			values[7] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getString(10);
			values[11] = rs.getDate(11);
			values[12] = rs.getString(12);
			values[13] =
				((rs.getString(13) != null)
					? new Integer(rs.getInt(13))
					: null);
			values[14] = rs.getString(14);
			values[15] = rs.getString(15);
			values[16] =
				((rs.getString(16) != null)
					? new Integer(rs.getInt(16))
					: null);
			values[17] = rs.getString(17);
			values[18] = rs.getString(18);
			values[19] =
				((rs.getString(19) != null)
					? new Integer(rs.getInt(19))
					: null);
			values[20] = rs.getString(20);
			values[21] = rs.getString(21);
			values[22] =
				((rs.getString(22) != null)
					? new Double(rs.getDouble(22))
					: null);
			values[23] =
				((rs.getString(23) != null)
					? new Double(rs.getDouble(23))
					: null);
			values[24] = rs.getString(24);
			rs.close();

			query =
				"SELECT Person_ID, Name FROM Person_View NATURAL JOIN Adoptor WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector adoVec = new Vector();
			while (rs.next()) {
				adoVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			rs.close();
			values[10] = ((adoVec.size() > 0) ? adoVec : null);
			rs.close();

			conn.releaseStatement();
		} catch (SQLException _e) {
			fullAdoptionPool.removeMe(this);
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
	public static class FullAdoptionFinder implements Finder {
		int id;
		public FullAdoptionFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (
				o instanceof FullAdoptionRecord
					&& ((FullAdoptionRecord) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return fullAdoptionPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		fullAdoptionPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 * @throws AccessDeniedException where user not allowed access to this row
	 */
	public static FullAdoptionRecord getFullAdoptionRecord(
		int id,
		User user,
		PageState state)
		throws SQLException, IOException, AccessDeniedException {
		FullAdoptionRecord a =
			(FullAdoptionRecord) fullAdoptionPool.retrieve(
				new FullAdoptionFinder(id));
		if (a == null) {
			a = new FullAdoptionRecord(id, state);
		}
		if (a.get(FEATURE_SECURITY_CLASS_ID) != null
			&& a.get(SECURITY_CLASS_ID) != null
			&& (!FREDUtils
				.isAllowedToView(
					user,
					a.getAsInt(FEATURE_SECURITY_CLASS_ID),
					state)
				|| !FREDUtils.isAllowedToView(
					user,
					a.getAsInt(SECURITY_CLASS_ID),
					state))) {
			throw new AccessDeniedException();
		}
		return a;
	}

	public String toString() {
		return (values[RECORD_ID]).toString();
	}

}
