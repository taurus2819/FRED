package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents an Audit record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class Audit implements FREDConstants {

	public static final int AUDIT_ID = 0;
	public static final int STATUS = 1;
	public static final int DATA_ORIGIN_ID = 2;
	public static final int DATA_ORIGIN = 3;
	public static final int CREATED_BY_ID = 4;
	public static final int CREATED_BY = 5;
	public static final int CREATED_DATE = 6;
	public static final int MODIFIED_BY_ID = 7;
	public static final int MODIFIED_BY = 8;
	public static final int MODIFIED_DATE = 9;
	public static final int SUBMITTED_BY_ID = 10;
	public static final int SUBMITTED_BY = 11;
	public static final int SUBMITTED_DATE = 12;
	public static final int APPROVED_BY_ID = 13;
	public static final int APPROVED_BY = 14;
	public static final int APPROVED_DATE = 15;
	public static final int LAST_CHANGE = 16;
	public static final int WORKING_COMMENTS = 17;
	public static final int WORKING_FOLDER_ID = 18;

	private PageState state;
	private static Pool auditPool = new Pool();
	private int id;
	private Object[] values = new Object[19];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected Audit(int id, PageState state) throws SQLException, IOException {
		this.state = state;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		auditPool.add(this);
		String query =
			"SELECT AUDIT_ID, STATUS, DATA_ORIGIN_ID, DATA_ORIGIN, CREATED_BY_ID, CREATED_BY, CREATED_DATE, "
				+ "MODIFIED_BY_ID, MODIFIED_BY, MODIFIED_DATE, SUBMITTED_BY_ID, SUBMITTED_BY, SUBMITTED_DATE, "
				+ "APPROVED_BY_ID, APPROVED_BY, APPROVED_DATE, LAST_CHANGE, WORKING_COMMENTS, WORKING_FOLDER_ID "
				+ "FROM Audit_View WHERE Audit_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = rs.getString(2);
			values[2] =
				((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[3] = rs.getString(4);
			values[4] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[5] = rs.getString(6);
			values[6] = rs.getDate(7);
			values[7] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getDate(10);
			values[10] =
				((rs.getString(11) != null)
					? new Integer(rs.getInt(11))
					: null);
			values[11] = rs.getString(12);
			values[12] = rs.getDate(13);
			values[13] =
				((rs.getString(14) != null)
					? new Integer(rs.getInt(14))
					: null);
			values[14] = rs.getString(15);
			values[15] = rs.getDate(16);
			values[16] = rs.getDate(17);
			values[17] = rs.getString(18);
			values[18] =
				((rs.getString(19) != null)
					? new Integer(rs.getInt(19))
					: null);
			rs.close();
			conn.releaseStatement();
		} catch (SQLException _e) {
			auditPool.removeMe(this);
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
	public static class AuditFinder implements Finder {
		int id;
		public AuditFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof Audit && ((Audit) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return auditPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		auditPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Audit getAudit(int id, PageState state)
		throws SQLException, IOException {

		Audit a = (Audit) auditPool.retrieve(new AuditFinder(id));
		if (a != null) {
			return a;
		} else {
			return new Audit(id, state);
		}
	}

	public String toString() {
		return (values[STATUS]).toString();
	}

}
