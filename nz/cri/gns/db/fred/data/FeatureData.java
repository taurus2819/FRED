package nz.cri.gns.db.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Feature record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class FeatureData {

	private static Pool pool = new Pool();
	private int id;
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];
	private Object[] values = new Object[24];

	/**
	 * Cannot be called directly. use static getDate method instead.
	 */
	private FeatureData(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT FEATURE_ID, SITE_ID, AUDIT_ID, MASTERFILE_ID, LOCALITY, REG_AREA_ID, COMMENTS, "
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
			values[1] =
				((rs.getString(2) != null) ? new Integer(rs.getInt(2)) : null);
			values[2] =
				((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[3] =
				((rs.getString(4) != null) ? new Integer(rs.getInt(4)) : null);
			values[5] = rs.getString(5);
			values[6] =
				((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[7] = rs.getString(7);
			values[8] = rs.getString(8);
			values[9] = rs.getString(9);
			values[10] = rs.getString(10);
			values[11] = rs.getDate(11);
			values[12] = rs.getString(12);
			values[13] = rs.getDate(13);
			values[14] = rs.getString(14);
			values[15] =
				((rs.getString(15) != null)
					? new Integer(rs.getInt(15))
					: null);
			values[16] = rs.getString(16);
			values[17] =
				((rs.getString(17) != null)
					? new Double(rs.getDouble(17))
					: null);
			values[18] =
				((rs.getString(18) != null)
					? new Double(rs.getDouble(18))
					: null);
			values[19] =
				((rs.getString(19) != null)
					? new Double(rs.getDouble(19))
					: null);
			rs.close();
			query =
				"SELECT Security_Class_ID, Status FROM Audit_Table WHERE Audit_ID = ?";
			data[0] = (Integer) values[2];
			rs = conn.executeQuery(query, types, data);
			if (rs.next()) {
				values[20] =
					((rs.getString(1) != null)
						? new Integer(rs.getInt(1))
						: null);
				values[23] = rs.getString(2);
			}
			rs.close();
			if (values[3] != null) {
				query = "SELECT Name FROM Folder WHERE Folder_ID = ?";
				data[0] = (Integer) values[3];
				rs = conn.executeQuery(query, types, data);
				if (rs.next()) {
					values[4] = rs.getString(1);
				}
				rs.close();
			}
			query = "SELECT Sample_ID FROM Sample_All_View WHERE Feature_ID = ?";
			data[0] = values[0];
			rs = conn.executeQuery(query, types, data);
			Vector rec = new Vector();
			while (rs.next()) {
				rec.add(new Integer(rs.getInt(1)));
			}
			values[21] = rec;
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
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	protected Object get(int field) throws IllegalArgumentException {
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
	protected static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof FeatureData && ((FeatureData) o).id == this.id);
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
	protected static FeatureData getData(int id, PageState state)
		throws SQLException, IOException {
		FeatureData f = (FeatureData) pool.retrieve(new DataFinder(id));
		if (f == null) {
			f = new FeatureData(id, state);
		}
		return f;
	}

	public String toString() {
		return (values[9]).toString();
	}
	
	public void finalize() throws Throwable {
		pool.removeMe(this);
	}

}
