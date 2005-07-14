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
public class TaxonomicLookupData {

	private static Pool pool = new Pool();
	private int id;
	private Object[] values = new Object[12];

	/**
	 * Cannot be called directly. use static getData method instead.
	 */
	private TaxonomicLookupData(int id, PageState state) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT t.taxa_id, t.group_id, l.name, t.taxonomic_name, t.author, t.status, t.submitted_by_id, "
				+ "u1.full_name, t.submitted_date, t.approved_by_id, u2.full_name, t.approved_date "
				+ "FROM taxonomic_lookup t, taxonomic_group l, fr_user_view u1, fr_user_view u2 "
				+ "WHERE t.group_id = l.group_id AND t.submitted_by_id = u1.pe_id(+) AND t.approved_by_id = u2.pe_id(+) "
				+ "AND t.taxa_id = ?";
		try {
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(id)});
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + id);
			}
			values[TaxonomicLookup.TAXA_ID] = new Integer(rs.getInt(1));
			values[TaxonomicLookup.GROUP_ID] = new Integer(rs.getInt(2));
			values[TaxonomicLookup.GROUP_NAME] = rs.getString(3);
			values[TaxonomicLookup.TAXONOMIC_NAME] = rs.getString(4);
			values[TaxonomicLookup.AUTHOR] = rs.getString(5);
			values[TaxonomicLookup.STATUS] = rs.getString(6);
			values[TaxonomicLookup.SUBMITTED_BY_ID] = ((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[TaxonomicLookup.SUBMITTED_BY] = rs.getString(8);
			values[TaxonomicLookup.SUBMITTED_DATE] = rs.getDate(9);
			values[TaxonomicLookup.APPROVED_BY_ID] = ((rs.getString(10) != null) ? new Integer(rs.getInt(10)) : null);
			values[TaxonomicLookup.APPROVED_BY] = rs.getString(11);
			values[TaxonomicLookup.APPROVED_DATE] = rs.getDate(12);
			rs.close();
			conn.releaseStatement();
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
			return (o instanceof TaxonomicLookupData && ((TaxonomicLookupData) o).id == this.id);
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
	protected static TaxonomicLookupData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		TaxonomicLookupData tl = (TaxonomicLookupData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && tl != null) {
			pool.removeMe(tl);
			tl = null;
		}
		if (tl == null)
			tl = new TaxonomicLookupData(id, state);
		return tl;
	}
		

	public String toString() {
		return (values[TaxonomicLookup.TAXONOMIC_NAME]).toString();
	}

}
