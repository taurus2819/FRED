package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampleView method instead.
 */
public class SampleData {

	private static Pool pool = new Pool();
	private int id;
	private Object[] values = new Object[63];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	private SampleData(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT FEATURE_ID, SAMPLE_ID, FEATURE_TYPE, SAMPLE_NAME, FR_ID, FR_NUMBER, "
				+ "YARD_FR_ID, YARD_FR_NUMBER, FEATURE_NAME, MAP_SHEET, SERIAL_NUMBER, RECOLLECTION_NUMBER, "
				+ "YARD_MAP_SHEET, YARD_SERIAL_NUMBER, YARD_RECOLLECTION_NUMBER, DRILLHOLE_DEPTH, TOP_DEPTH, "
				+ "BOTTOM_DEPTH, DRILL_TYPE, MASTERFILE_ID, MASTERFILE_NAME, REG_AREA_ID, REG_AREA_NAME, "
				+ "AUDIT_ID, STATUS, LAST_CHANGE, WORKING_FOLDER_ID, WORKING_COMMENTS, SECURITY_CLASS_ID, "
				+ "SITE_ID, LATITUDE, LONGITUDE, QMAP_SHEET, NZMG_SHEET, NZMG_EAST, NZMG_NORTH, METHOD, "
				+ "ACCURACY, LOCALITY, DRILLHOLE_LICENCE_NAME, PERSON_ID, PERSON, START_DATE, START_DATE_ROUNDING, "
				+ "FINISH_DATE, FINISH_DATE_ROUNDING, DATUM_TYPE, DATUM_ELEVATION, START_DEPTH, FINISH_DEPTH, "
				+ "METHOD_ID, ORIG_SYSTEM_ID, COORD_SYSTEM, ORIG_COORD, COUNTRY_CODE, COUNTRY_NAME, REG_AREA_CODE, "
				+ "DRILL_TYPE_ID FROM Sample_All_View WHERE Sample_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next())
				throw new SQLException("Cannot find record in database with this id: " + this.id);
			values[Sample.FEATURE_ID] = new Integer(rs.getInt(1));
			values[Sample.SAMPLE_ID] = new Integer(rs.getInt(2));
			values[Sample.FEATURE_TYPE] = rs.getString(3);
			values[Sample.SAMPLE_NAME] = rs.getString(4);
			values[Sample.FR_ID] = ((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[Sample.FR_NUMBER] = rs.getString(6);
			values[Sample.YARD_FR_ID] = ((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[Sample.YARD_FR_NUMBER] = rs.getString(8);
			values[Sample.FEATURE_NAME] = rs.getString(9);
			values[Sample.MAP_SHEET] = rs.getString(10);
			values[Sample.SERIAL_NUMBER] = ((rs.getString(11) != null) ? new Integer(rs.getInt(11)) : null);
			values[Sample.RECOLLECTION_NUMBER] = rs.getString(12);
			values[Sample.YARD_MAP_SHEET] = rs.getString(13);
			values[Sample.YARD_SERIAL_NUMBER] = ((rs.getString(14) != null) ? new Integer(rs.getInt(14)) : null);
			values[Sample.YARD_RECOLLECTION_NUMBER] = rs.getString(15);
			values[Sample.DRILLHOLE_DEPTH] = rs.getString(16);
			values[Sample.TOP_DEPTH] = ((rs.getString(17) != null) ? new Double(rs.getDouble(17)) : null);
			values[Sample.BOTTOM_DEPTH] = ((rs.getString(18) != null) ? new Double(rs.getDouble(18)) : null);
			values[Sample.DRILL_TYPE_ID] = rs.getString(58);
			values[Sample.DRILL_TYPE] = rs.getString(19);
			values[Sample.MASTERFILE_ID] = ((rs.getString(20) != null) ? new Integer(rs.getInt(20)) : null);
			values[Sample.MASTERFILE_NAME] = rs.getString(21);
			values[Sample.REG_AREA_ID] = ((rs.getString(22) != null) ? new Integer(rs.getInt(22)) : null);
			values[Sample.REG_AREA_CODE] = rs.getString(57);
			values[Sample.REG_AREA_NAME] = rs.getString(23);
			values[Sample.AUDIT_ID] = new Integer(rs.getInt(24));
			values[Sample.STATUS] = rs.getString(25);
			values[Sample.LAST_CHANGE] = rs.getDate(26);
			values[Sample.WORKING_FOLDER_ID] = ((rs.getString(27) != null) ? new Integer(rs.getInt(27)) : null);
			values[Sample.WORKING_COMMENTS] = rs.getString(28);
			values[Sample.SECURITY_CLASS_ID] = ((rs.getString(29) != null) ? new Integer(rs.getInt(29)) : null);
			values[Sample.SITE_ID] = ((rs.getString(30) != null) ? new Integer(rs.getInt(30)) : null);
			values[Sample.LATITUDE] = ((rs.getString(31) != null) ? new Double(rs.getDouble(31)) : null);
			values[Sample.LONGITUDE] = ((rs.getString(32) != null) ? new Double(rs.getDouble(32)) : null);
			values[Sample.COUNTRY_CODE] = rs.getString(55);
			values[Sample.COUNTRY_NAME] = rs.getString(56);
			values[Sample.ORIG_SYSTEM_ID] = ((rs.getString(52) != null) ? new Integer(rs.getInt(52)) : null);
			values[Sample.COORD_SYSTEM] = rs.getString(53);
			values[Sample.ORIG_COORD] = rs.getString(54);
			values[Sample.QMAP_SHEET] = rs.getString(33);
			values[Sample.NZMG_SHEET] = rs.getString(34);
			values[Sample.NZMG_EAST] = ((rs.getString(35) != null) ? new Double(rs.getDouble(35)) : null);
			values[Sample.NZMG_NORTH] =	((rs.getString(36) != null)	? new Double(rs.getDouble(36)) : null);
			values[Sample.METHOD_ID] = ((rs.getString(51) != null) ? new Integer(rs.getInt(51)) : null);
			values[Sample.METHOD] = rs.getString(37);
			values[Sample.ACCURACY] = ((rs.getString(38) != null) ? new Double(rs.getDouble(38)): null);
			values[Sample.LOCALITY] = rs.getString(39);
			values[Sample.DRILLHOLE_LICENCE_NAME] = rs.getString(40);
			values[Sample.PERSON_ID] = ((rs.getString(41) != null) ? new Integer(rs.getInt(41)) : null);
			values[Sample.PERSON] = rs.getString(42);
			values[Sample.START_DATE] = rs.getDate(43);
			values[Sample.START_DATE_ROUNDING] = rs.getString(44);
			values[Sample.FINISH_DATE] = rs.getDate(45);
			values[Sample.FINISH_DATE_ROUNDING] = rs.getString(46);
			values[Sample.DATUM_TYPE] = rs.getString(47);
			values[Sample.DATUM_ELEVATION] = ((rs.getString(48) != null) ? new Double(rs.getDouble(48))	: null);
			values[Sample.START_DEPTH] = ((rs.getString(49) != null) ? new Double(rs.getDouble(49))	: null);
			values[Sample.FINISH_DATE] = ((rs.getString(50) != null) ? new Double(rs.getDouble(50))	: null);
			rs.close();
			query =
				"SELECT Record_ID, Record_Type, Status, Last_Change FROM Record_All_View WHERE Sample_ID = ? ORDER BY Record_Type, Record_Name";
			rs = conn.executeQuery(query, types, data);
			Vector rec = new Vector();
			Vector wRec = new Vector();
			while (rs.next()) {
				rec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				if (!rs.getString(3).equals("approved"))
					wRec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				if (rs.getString(2).equals("SMP")) {
					values[Sample.SAMPLE_PROPERTY_RECORD_ID] = new Integer(rs.getInt(1));
					values[Sample.SAMPLE_PROPERTY_RECORD_STATUS] = rs.getString(3);
					values[Sample.SAMPLE_PROPERTY_RECORD_LAST_CHANGE] = rs.getDate(4);
				} 
			}
			values[Sample.RECORDS] = rec;
			values[Sample.WORKING_RECORDS] = wRec;
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
	protected java.util.Date getAsDate(int field)
		throws IllegalArgumentException {
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
			return (o instanceof SampleData && ((SampleData) o).id == this.id);
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
	protected static SampleData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		SampleData s = (SampleData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && s != null) {
			pool.removeMe(s);
			s = null;
		}
		if (s == null) {
			s = new SampleData(id, state);
		}
		return s;
	}

	public String toString() {
		return (values[5]).toString();
	}

	//public void finalize() throws Throwable {
	//	pool.removeMe(this);
	//}

}