package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents an Audit record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class Audit {

	public static final int AUDIT_ID = 0;
	public static final int STATUS = 1;
	public static final int DATA_ORIGIN_ID = 2;
	public static final int DATA_ORIGIN = 3;
	public static final int CREATED_BY_ID = 4;
	public static final int CREATED_BY = 5;
	public static final int CREATED_DATE = 6;
	public static final int EDIT_HISTORY = 19;
	public static final int EDITED_BY_ID = 7;
	public static final int EDITED_BY = 8;
	public static final int EDITED_DATE = 9;
	public static final int SUBMITTED_BY_ID = 10;
	public static final int SUBMITTED_BY = 11;
	public static final int SUBMITTED_DATE = 12;
	public static final int APPROVED_BY_ID = 13;
	public static final int APPROVED_BY = 14;
	public static final int APPROVED_DATE = 15;
	public static final int WORKING_COMMENTS = 17;
	public static final int WORKING_FOLDER_ID = 18;
	public static final int CURATOR_COMMENTS = 20;

	public static final String STATUS_WORKING = "working";
	public static final String STATUS_WAITING = "waiting";
	public static final String STATUS_REJECTED = "rejected";
	public static final String STATUS_APPROVED = "approved";
	

	private PageState state;
	private int id;
	private Object[] values = new Object[21];

	/**
	 * Cannot be called directly. use static getAudit method instead.
	 */
	protected Audit(int id, PageState state) throws SQLException, IOException {
		this.state = state;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		String query =
			"SELECT audit_id, status, data_origin_id, data_origin, created_by_id, created_by, created_date, "
				+ "edited_by_id, edited_by, edited_date, edit_comments, submitted_by_id, submitted_by, submitted_date, "
				+ "approved_by_id, approved_by, approved_date, working_comments, working_folder_id, curator_comments "
				+ "FROM audit_view WHERE audit_id = ? ORDER BY nvl(edited_date, '01-01-1000')";
		try {
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(id)});
			Vector edits = new Vector();
			while (rs.next()) {		
				values[AUDIT_ID] = new Integer(rs.getInt(1));
				values[STATUS] = rs.getString(2);
				values[DATA_ORIGIN_ID] = ((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
				values[DATA_ORIGIN] = rs.getString(4);
				values[CREATED_BY_ID] =	((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
				values[CREATED_BY] = rs.getString(6);
				values[CREATED_DATE] = rs.getDate(7);
				values[EDITED_BY_ID] =	((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
				values[EDITED_BY] = rs.getString(9);
				values[EDITED_DATE] = rs.getDate(10);
				values[SUBMITTED_BY_ID] = ((rs.getString(12) != null) ? new Integer(rs.getInt(12)) : null);
				values[SUBMITTED_BY] = rs.getString(13);
				values[SUBMITTED_DATE] = rs.getDate(14);
				values[APPROVED_BY_ID] = ((rs.getString(15) != null) ? new Integer(rs.getInt(15)) : null);
				values[APPROVED_BY] = rs.getString(16);
				values[APPROVED_DATE] = rs.getDate(17);
				values[WORKING_COMMENTS] = rs.getString(18);
				values[WORKING_FOLDER_ID] = ((rs.getString(19) != null)	? new Integer(rs.getInt(19)) : null);
				values[CURATOR_COMMENTS] = rs.getString(20);
				if ((rs.getString(8) != null) || rs.getString(10) != null) {
					AuditEdit ae = new AuditEdit();
					ae.setEditedByID(((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null));
					ae.setEditedBy(rs.getString(9));
					ae.setEditedDate(rs.getDate(10));
					ae.setComments(rs.getString(11));
					edits.add(ae);
				}
			}
			rs.close();
			values[EDIT_HISTORY] = ((edits.size() > 0) ? edits : null);
			if (values[AUDIT_ID] == null)
				throw new SQLException("Cannot find record in database with this id: " + this.id);
			conn.releaseStatement();
		} catch (SQLException _e) {
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
	public Object get(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		return values[field];
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Audit getAudit(int id, PageState state) throws SQLException, IOException {
		return new Audit(id, state);
	}

	public String toString() {
		return (values[STATUS]).toString();
	}

}
