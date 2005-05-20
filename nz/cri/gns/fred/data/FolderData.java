package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Folder.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getFolder method instead.
 */
public class FolderData {

	private static Pool pool = new Pool();
	private int id;
	private Object[] values = new Object[6];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getFolder method instead.
	 */
	private FolderData(int id, PageState state) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT f.folder_id, f.name, f.folder_type, f.owner_id, pv.full_name "
				+ "FROM folder f, ip.person_view pv WHERE f.owner_id = pe_id(+) AND folder_id = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[Folder.FOLDER_ID] = new Integer(rs.getInt(1));
			values[Folder.NAME] = rs.getString(2);
			values[Folder.FOLDER_TYPE] = rs.getString(3);
			values[Folder.OWNER_ID] = ((rs.getString(4) != null) ? new Integer(rs.getInt(4)) : null);
			values[Folder.OWNER] = rs.getString(5);
			rs.close();
			query = "SELECT DISTINCT feature_id, sample_name FROM folder_content_view WHERE folder_id = ? ORDER BY UPPER(sample_name)";
			rs = conn.executeQuery(query, types, data);
			Vector feats = new Vector();
			while (rs.next()) {
				Integer featureId = new Integer(rs.getInt(1));
				System.out.println(featureId + ": " + rs.getString(2));
				if (!feats.contains(featureId))
					feats.add(featureId);
			}
			values[Folder.FEATURES] = feats;
			rs.close();
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
	protected int getAsInt(int field) throws IllegalArgumentException {
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
	protected double getAsDouble(int field) throws IllegalArgumentException {
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
	protected java.util.Date getAsDate(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (java.util.Date) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	protected Vector getAsVector(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (Vector) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	protected String getAsString(int field) throws IllegalArgumentException {
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
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	protected Object get(int field) throws IllegalArgumentException {
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
	protected static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof FolderData && ((FolderData) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	protected static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	protected static void purge() {
		pool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	protected static FolderData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		FolderData f = (FolderData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && f != null) {
			pool.removeMe(f);
			f = null;
		}
		if (f == null)
			f = new FolderData(id, state);
		return f;
	}
		

	public String toString() {
		return (values[1]).toString();
	}

}
