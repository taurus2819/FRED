package nz.cri.gns.db.fred;

import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampleView method instead.
 */
public class SampleView {

	public static final int FEATURE_ID = 0;
	public static final int SAMPLE_ID = 1;
	public static final int FEATURE_TYPE = 2;
	public static final int SAMPLE_NAME = 3;
	public static final int FR_ID = 4;
	public static final int FR_NUMBER = 5;
	public static final int YARD_FR_ID = 6;
	public static final int YARD_FR_NUMBER = 7;
	public static final int FEATURE_NAME = 8;
	public static final int MAP_SHEET = 9;
	public static final int SERIAL_NUMBER = 10;
	public static final int RECOLLECTION_NUMBER = 11;
	public static final int YARD_MAP_SHEET = 12;
	public static final int YARD_SERIAL_NUMBER = 13;
	public static final int YARD_RECOLLECTION_NUMBER = 14;
	public static final int DRILLHOLE_DEPTH = 15;
	public static final int TOP_DEPTH = 16;
	public static final int BOTTOM_DEPTH = 17;
	public static final int DRILL_TYPE = 18;
	public static final int MASTERFILE_ID = 19;
	public static final int MASTERFILE_NAME = 20;
	public static final int REG_AREA_ID = 21;
	public static final int REG_AREA_NAME = 22;
	public static final int AUDIT_ID = 23;
	public static final int STATUS = 24;
	public static final int LAST_CHANGE = 25;
	public static final int WORKING_FOLDER_ID = 26;
	public static final int WORKING_COMMENTS = 27;
	public static final int SECURITY_CLASS_ID = 28;
	public static final int SITE_ID = 29;
	public static final int LATITUDE = 30;
	public static final int LONGITUDE = 31;
	public static final int QMAP_SHEET = 32;
	public static final int NZMG_SHEET = 33;
	public static final int NZMG_EAST = 34;
	public static final int NZMG_NORTH = 35;
	public static final int METHOD = 36;
	public static final int ACCURACY = 37;
	public static final int LOCALITY = 38;
	public static final int DRILLHOLE_LICENCE_NAME = 39;
	public static final int PERSON_ID = 40;
	public static final int PERSON = 41;
	public static final int START_DATE = 42;
	public static final int START_DATE_ROUNDING = 43;
	public static final int FINISH_DATE = 44;
	public static final int FINISH_DATE_ROUNDING = 45;
	public static final int DATUM_TYPE = 46;
	public static final int DATUM_ELEVATION = 47;
	public static final int START_DEPTH = 48;
	public static final int FINISH_DEPTH = 49;

	protected static Pool sampleViewPool = new Pool();
	protected int id;
	private Object[] values = new Object[50];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected SampleView(int id, DatabaseApp2 conn) throws SQLException {
		this.id = id;
		sampleViewPool.add(this);
		String query =
			"SELECT FEATURE_ID, SAMPLE_ID, FEATURE_TYPE, SAMPLE_NAME, FR_ID, FR_NUMBER, "
				+ "YARD_FR_ID, YARD_FR_NUMBER, FEATURE_NAME, MAP_SHEET, SERIAL_NUMBER, RECOLLECTION_NUMBER, "
				+ "YARD_MAP_SHEET, YARD_SERIAL_NUMBER, YARD_RECOLLECTION_NUMBER, DRILLHOLE_DEPTH, TOP_DEPTH, "
				+ "BOTTOM_DEPTH, DRILL_TYPE, MASTERFILE_ID, MASTERFILE_NAME, REG_AREA_ID, REG_AREA_NAME, "
				+ "AUDIT_ID, STATUS, LAST_CHANGE, WORKING_FOLDER_ID, WORKING_COMMENTS, SECURITY_CLASS_ID, "
				+ "SITE_ID, LATITUDE, LONGITUDE, QMAP_SHEET, NZMG_SHEET, NZMG_EAST, NZMG_NORTH, METHOD, "
				+ "ACCURACY, LOCALITY, DRILLHOLE_LICENCE_NAME, PERSON_ID, PERSON, START_DATE, START_DATE_ROUNDING, "
				+ "FINISH_DATE, FINISH_DATE_ROUNDING, DATUM_TYPE, DATUM_ELEVATION, START_DEPTH, FINISH_DEPTH "
				+ "FROM Sample_All_View WHERE Sample_ID = "
				+ this.id;
		try {
			ResultSet rs = conn.executeQuery(query);
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
			conn.releaseStatement();
		} catch (SQLException _e) {
			sampleViewPool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field, User user) {
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
	public double getAsDouble(int field, User user) {
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
	public java.util.Date getAsDate(int field, User user) {
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
	public String getAsString(int field, User user) {
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
	public Object get(int field, User user) {
		if (values.length < field)
			throw new IllegalArgumentException("Invalid field");
		return values[field];
	}

	/**
	 * Inner class used for object pooling.
	 */
	public static class SampleViewFinder implements Finder {
		int id;
		public SampleViewFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof SampleView && ((SampleView) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return sampleViewPool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		sampleViewPool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static SampleView getSampleView(int id, DatabaseApp2 conn)
		throws SQLException {

		SampleView sv =
			(SampleView) sampleViewPool.retrieve(new SampleViewFinder(id));
		if (sv != null) {
			return sv;
		} else {
			return new SampleView(id, conn);
		}
	}

	public String toString() {
		return (values[FEATURE_NAME]).toString();
	}

}