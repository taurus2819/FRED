package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampleView method instead.
 */
public class FullSampleData {

	private static Pool fullSampleDataPool = new Pool();
	private int id;
	private Object[] values = new Object[51];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	private FullSampleData(int id, PageState state) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		fullSampleDataPool.add(this);
		String query =
			"SELECT FEATURE_ID, SAMPLE_ID, FEATURE_TYPE, SAMPLE_NAME, FR_ID, FR_NUMBER, "
				+ "YARD_FR_ID, YARD_FR_NUMBER, FEATURE_NAME, MAP_SHEET, SERIAL_NUMBER, RECOLLECTION_NUMBER, "
				+ "YARD_MAP_SHEET, YARD_SERIAL_NUMBER, YARD_RECOLLECTION_NUMBER, DRILLHOLE_DEPTH, TOP_DEPTH, "
				+ "BOTTOM_DEPTH, DRILL_TYPE, MASTERFILE_ID, MASTERFILE_NAME, REG_AREA_ID, REG_AREA_NAME, "
				+ "AUDIT_ID, STATUS, LAST_CHANGE, WORKING_FOLDER_ID, WORKING_COMMENTS, SECURITY_CLASS_ID, "
				+ "SITE_ID, LATITUDE, LONGITUDE, QMAP_SHEET, NZMG_SHEET, NZMG_EAST, NZMG_NORTH, METHOD, "
				+ "ACCURACY, LOCALITY, DRILLHOLE_LICENCE_NAME, PERSON_ID, PERSON, START_DATE, START_DATE_ROUNDING, "
				+ "FINISH_DATE, FINISH_DATE_ROUNDING, DATUM_TYPE, DATUM_ELEVATION, START_DEPTH, FINISH_DEPTH "
				+ "FROM Sample_All_View WHERE Status = 'approved' AND Sample_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = new Integer(rs.getInt(2));
			values[2] = rs.getString(3);
			values[3] = rs.getString(4);
			values[4] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[5] = rs.getString(6);
			values[6] =
				((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[7] = rs.getString(8);
			values[8] = rs.getString(9);
			values[9] = rs.getString(10);
			values[10] =
				((rs.getString(11) != null)
					? new Integer(rs.getInt(11))
					: null);
			values[11] = rs.getString(12);
			values[12] = rs.getString(13);
			values[13] =
				((rs.getString(14) != null)
					? new Integer(rs.getInt(14))
					: null);
			values[14] = rs.getString(15);
			values[15] = rs.getString(16);
			values[16] =
				((rs.getString(17) != null)
					? new Double(rs.getDouble(17))
					: null);
			values[17] =
				((rs.getString(18) != null)
					? new Double(rs.getDouble(18))
					: null);
			values[18] = rs.getString(19);
			values[19] =
				((rs.getString(20) != null)
					? new Integer(rs.getInt(20))
					: null);
			values[20] = rs.getString(21);
			values[21] =
				((rs.getString(22) != null)
					? new Integer(rs.getInt(22))
					: null);
			values[22] = rs.getString(23);
			values[23] = new Integer(rs.getInt(24));
			values[24] = rs.getString(25);
			values[25] = rs.getString(26);
			values[26] =
				((rs.getString(27) != null)
					? new Integer(rs.getInt(27))
					: null);
			values[27] = rs.getString(28);
			values[28] =
				((rs.getString(29) != null)
					? new Integer(rs.getInt(29))
					: null);
			values[29] =
				((rs.getString(30) != null)
					? new Integer(rs.getInt(30))
					: null);
			values[30] =
				((rs.getString(31) != null)
					? new Double(rs.getDouble(31))
					: null);
			values[31] =
				((rs.getString(32) != null)
					? new Double(rs.getDouble(32))
					: null);
			values[32] = rs.getString(33);
			values[33] = rs.getString(34);
			values[34] =
				((rs.getString(35) != null)
					? new Double(rs.getDouble(35))
					: null);
			values[35] =
				((rs.getString(36) != null)
					? new Double(rs.getDouble(36))
					: null);
			values[36] = rs.getString(37);
			values[37] =
				((rs.getString(38) != null)
					? new Integer(rs.getInt(38))
					: null);
			values[38] = rs.getString(39);
			values[39] = rs.getString(40);
			values[40] =
				((rs.getString(41) != null)
					? new Integer(rs.getInt(41))
					: null);
			values[41] = rs.getString(42);
			values[42] = rs.getDate(43);
			values[43] = rs.getString(44);
			values[44] = rs.getDate(45);
			values[45] = rs.getString(46);
			values[46] = rs.getString(47);
			values[47] =
				((rs.getString(48) != null)
					? new Double(rs.getDouble(48))
					: null);
			values[48] =
				((rs.getString(49) != null)
					? new Double(rs.getDouble(49))
					: null);
			values[49] =
				((rs.getString(50) != null)
					? new Double(rs.getDouble(50))
					: null);
			rs.close();
			query = "SELECT Record_ID, Record_Type FROM Record_View WHERE Status = 'approved' AND Sample_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector rec = new Vector();
			while (rs.next()) {
				rec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			values[50] = rec;
			rs.close();
			conn.releaseStatement();
		} catch (SQLException _e) {
			fullSampleDataPool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	protected int getAsInt(int field) throws IllegalArgumentException {
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
	protected double getAsDouble(int field) throws IllegalArgumentException {
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
	protected java.util.Date getAsDate(int field) throws IllegalArgumentException {
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
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	protected Vector getAsVector(int field) throws IllegalArgumentException {
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
	protected String getAsString(int field) throws IllegalArgumentException {
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
	protected Object get(int field) throws IllegalArgumentException {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		return values[field];
	}

	/**
	 * Inner class used for object pooling.
	 */
	protected static class FullSampleDataFinder implements Finder {
		int id;
		public FullSampleDataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof FullSampleData && ((FullSampleData) o).id == this.id);
		}
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	protected static int getPoolSize() {
		return fullSampleDataPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	protected static void purge() {
		fullSampleDataPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	protected static FullSampleData getFullSampleData(int id, PageState state)
		throws SQLException, IOException {
		FullSampleData f =
			(FullSampleData) fullSampleDataPool.retrieve(new FullSampleDataFinder(id));
		if (f == null) {
			f = new FullSampleData(id, state);
		}
		return f;
	}

	public String toString() {
		return (values[5]).toString();
	}

	public void finalize() throws Throwable {
		fullSampleDataPool.removeMe(this);
	}

}