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
 * Class that represents a Taxonomic Panel.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class TaxaPanelData {

	private static Pool pool = new Pool();
	private int id;
	private Object[] values = new Object[6];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getFolder method instead.
	 */
	private TaxaPanelData(int id, PageState state) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT tp.group_id, l.name FROM taxa_panel tp, lookup l WHERE tp.group_id = l.lookup_id AND tp.group_id = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[TaxaPanel.PANEL_ID] = new Integer(rs.getInt(1));
			values[TaxaPanel.NAME] = rs.getString(2);
			rs.close();
			query = "SELECT taxa_id, taxonomic_name, status FROM taxonomic_lookup WHERE group_id = ? ORDER BY UPPER(taxonomic_name)";
			rs = conn.executeQuery(query, types, data);
			Vector appVec = new Vector();
			Vector provVec = new Vector();
			Vector rejVec = new Vector();
			Vector obVec = new Vector();
			while (rs.next()) {
				if (rs.getString(3).equals(TaxonomicLookup.APPROVED_STATUS)) {
					appVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				} else if (rs.getString(3).equals(TaxonomicLookup.PROVISIONAL_STATUS)) {
					provVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				} else if (rs.getString(3).equals(TaxonomicLookup.REJECTED_STATUS)) {
					rejVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				} else if (rs.getString(3).equals(TaxonomicLookup.OBSOLETE_STATUS)) {
					obVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				}
			}
			values[TaxaPanel.APPROVED_TAXA] = appVec;
			values[TaxaPanel.PROVISIONAL_TAXA] = provVec;
			values[TaxaPanel.REJECTED_TAXA] = rejVec;
			values[TaxaPanel.OBSOLETE_TAXA] = obVec;
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
			return (o instanceof TaxaPanelData && ((TaxaPanelData) o).id == this.id);
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
	protected static TaxaPanelData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		TaxaPanelData tp = (TaxaPanelData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && tp != null) {
			pool.removeMe(tp);
			tp = null;
		}
		if (tp == null)
			tp = new TaxaPanelData(id, state);
		return tp;
	}
		

	public String toString() {
		return (values[TaxaPanel.NAME]).toString();
	}

}
