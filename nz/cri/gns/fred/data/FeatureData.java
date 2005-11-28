package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.db.metadata.DocumentAttacher;
import nz.cri.gns.db.metadata.MetadataRecord;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Feature record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class FeatureData implements FeatureConstants {

	//private static Pool pool = new Pool();
	private int id;
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];
	private Object[] values = new Object[30];
	protected MetadataRecord[] mr;

	/**
	 * Cannot be called directly. use static getDate method instead.
	 */
	private FeatureData(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		//pool.add(this);
		System.out.println("FeatureData: " + new java.util.Date() + ": About to execute query");
		String query =
			"SELECT f.feature_id, f.site_id, f.audit_id, f.masterfile_id, fd.name, f.locality, f.reg_area_id, f.comments, "
				+ "f.feature_type, f.feature_name, f.drillhole_licence_name, f.start_date, f.start_date_rounding, f.finish_date, "
				+ "f.finish_date_rounding, f.person_id, f.datum_type, f.datum_elevation, f.start_depth, f.finish_depth, "
				+ "a.security_class_id, a.status, a.working_folder_id, a.created_date, f.orig_system_id, f.orig_coord, f.map_year "
				+ "FROM feature f, audit_table a, folder fd "
				+ "WHERE f.audit_id = a.audit_id AND f.masterfile_id = fd.folder_id(+) AND feature_id = ?";
		System.out.println(query);
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			System.out.println("FeatureData: " + new java.util.Date() + ": Executed query");
			values[FEATURE_ID] = new Integer(rs.getInt(1));
			values[SITE_ID] = ((rs.getString(2) != null) ? new Integer(rs.getInt(2)) : null);
			values[AUDIT_ID] = ((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[MASTERFILE_ID] = ((rs.getString(4) != null) ? new Integer(rs.getInt(4)) : null);
			values[MASTERFILE_NAME] = rs.getString(5);
			values[LOCALITY] = rs.getString(6);
			values[REG_AREA_ID] = ((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[COMMENTS] = rs.getString(8);
			values[FEATURE_TYPE] = rs.getString(9);
			values[FEATURE_NAME] = rs.getString(10);
			values[DRILLHOLE_LICENCE_NAME] = rs.getString(11);
			values[START_DATE] = rs.getDate(12);
			values[START_DATE_ROUNDING] = rs.getString(13);
			values[FINISH_DATE] = rs.getDate(14);
			values[FINISH_DATE_ROUNDING] = rs.getString(15);
			values[PERSON_ID] = ((rs.getString(16) != null) ? new Integer(rs.getInt(16)) : null);
			values[DATUM_TYPE] = rs.getString(17);
			values[DATUM_ELEVATION] = ((rs.getString(18) != null) ? new Double(rs.getDouble(18)) : null);
			values[START_DEPTH] = ((rs.getString(19) != null) ? new Double(rs.getDouble(19)) : null);
			values[FINISH_DEPTH] = ((rs.getString(20) != null) ? new Double(rs.getDouble(20)) : null);
			values[SECURITY_CLASS_ID] = ((rs.getString(21) != null) ? new Integer(rs.getInt(21)) : null);
			values[STATUS] = rs.getString(22);
			values[WORKING_FOLDER_ID] = ((rs.getString(23) != null) ? new Integer(rs.getInt(23)) : null);
			values[CREATED_DATE] = rs.getDate(24);
			values[ORIG_SYSTEM_ID] = ((rs.getString(25) != null) ? new Integer(rs.getInt(25)) : null);
			values[ORIG_COORD] = rs.getString(26);
			values[MAP_YEAR] = ((rs.getString(27) != null) ? new Integer(rs.getInt(27)) : null);
			rs.close();
			query = "SELECT sample_id FROM sample WHERE feature_id = ? ORDER BY top_depth";
			data[0] = values[FEATURE_ID];
			rs = conn.executeQuery(query, types, data);
			Vector samp = new Vector();
			while (rs.next()) {
				samp.add(new Integer(rs.getInt(1)));
			}
			values[SAMPLES] = samp;
			rs.close();
			query = "SELECT DISTINCT sample_name FROM feature_view WHERE feature_id = ? ORDER BY sample_name";
			data[0] = values[FEATURE_ID];
			rs = conn.executeQuery(query, types, data);
			if (rs.next()) {
				String sampName = rs.getString(1);
				while (rs.next()) {
					sampName += ", " + rs.getString(1);
				}
				values[SAMPLE_NAMES] = sampName;
			}
			rs.close();
			if (values[FEATURE_NAME] != null) {
				query = "SELECT well_name FROM petroleum.petroleum_well WHERE UPPER(well_name) = ?";
				types[0] = Types.VARCHAR;
				data[0] = values[FEATURE_NAME].toString().toUpperCase();
				try {
					rs = conn.executeQuery(query, types, data);
					rs.next();
					values[PETWELL_LINK] = "/seismic/petwell.jsp?wellname=" + rs.getString(1);
					rs.close();
				} catch (Exception e) {
					values[PETWELL_LINK] = null;
				}
			}
			conn.releaseStatement();
		} catch (SQLException _e) {
			//pool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
		try {
			DocumentAttacher recordAttacher = FREDUtils.createFREDFeatureDocumentAttacher(state.session, state.context);
			mr = recordAttacher.getDocumentsForId(id);
		} catch (Exception e) {
			System.out.println(e);
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
	 *
	protected static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 *
	protected static void purge() {
		pool.removeAllElements();
	}
*/
	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	protected static FeatureData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		/*
		FeatureData f = (FeatureData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && f != null) {
			pool.removeMe(f);
			f = null;
		}
		if (f == null)
			f = new FeatureData(id, state);
		return f;
		*/
		return new FeatureData(id, state);
	}

	public String toString() {
		return (values[FEATURE_NAME] == null) ? "Unnamed feature" : values[FEATURE_NAME].toString();
	}

}
