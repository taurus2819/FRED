package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
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
public class Folder implements FREDConstants {

	public static final int FOLDER_ID = 0;
	public static final int NAME = 1;
	public static final int FOLDER_TYPE = 2;
	public static final int OWNER_ID = 3;
	public static final int OWNER = 4;
	
	private PageState state;
	private static Pool folderPool = new Pool();
	private int id;
	private Object[] values = new Object[5];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];
	private int userRights = 0;

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected Folder(int id, PageState state)
		throws SQLException, IOException {
		this.state = state;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		folderPool.add(this);
		String query =
			"SELECT Folder_ID, Name, Folder_Type, Owner_ID, Full_Name "
				+ "FROM Folder JOIN IP.Person_View ON Owner_ID = PE_ID WHERE Folder_ID = ?";
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
			values[4] = rs.getString(5);
			rs.close();
			conn.releaseStatement();
		} catch (SQLException _e) {
			folderPool.removeMe(this);
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
			throw new IllegalArgumentException(
				"Field cannot be returned as an int, class is "
					+ values[field].getClass().getName());
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
			throw new IllegalArgumentException(
				"Field cannot be returned as an double, class is "
					+ values[field].getClass().getName());
		}
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		try {
			return (java.util.Date) values[field];
		} catch (Exception _e) {
			throw new IllegalArgumentException(
				"Field cannot be returned as a Date, class is "
					+ values[field].getClass().getName());
		}
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IOException, SQLException {
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
	public static class FolderFinder implements Finder {
		int id;
		public FolderFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof Folder && ((Folder) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return folderPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		folderPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Folder getFolder(int id, User user, PageState state)
		throws SQLException, IOException {
		Folder f = (Folder) folderPool.retrieve(new FolderFinder(id));
		if (f == null) {
			f = new Folder(id, state);
		}
		f.userRights = FREDUtils.getUserFolderRights(user, f.getAsString(FOLDER_ID), state);
		return f;
	}

	public int getUserRights() {
		return userRights;
	}

	public boolean isAllowedEdit() {
		return ((userRights & 32) != 0);
	}

	public boolean isAllowedDelete() {
		return ((userRights & 32) != 0);
	}
		

	public String toString() {
		return (values[NAME]).toString();
	}

}
