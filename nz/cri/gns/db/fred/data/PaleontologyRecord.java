package nz.cri.gns.db.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Paleontology_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class PaleontologyRecord {

	public static final int RECORD_ID = 0;
	public static final int FEATURE_ID = 1;
	public static final int SAMPLE_ID = 2;
	public static final int FEATURE_STATUS = 3;
	public static final int FEATURE_SECURITY_CLASS_ID = 4;
	public static final int AUDIT_ID = 5;
	public static final int STATUS = 6;
	public static final int SECURITY_CLASS_ID = 7;
	public static final int SAMPLE_NAME = 8;
	public static final int DRILLHOLE_DEPTH = 9;
	public static final int IDENTIFIER = 10;
	public static final int IDENTIFICATION_DATE = 11;
	public static final int DATE_ROUNDING = 12;
	public static final int STAGE_ID = 13;
	public static final int STAGE = 14;
	public static final int STAGE_ABBREV = 15;
	public static final int STAGE_LOWER_ID = 16;
	public static final int STAGE_LOWER = 17;
	public static final int STAGE_LOWER_MOD = 18;
	public static final int STAGE_UPPER_ID = 19;
	public static final int STAGE_UPPER = 20;
	public static final int STAGE_UPPER_MOD = 21;
	public static final int AGE_START = 22;
	public static final int AGE_STOP = 23;
	public static final int STAGE_COMMENTS = 24;
	public static final int LAB_SECTION_ID = 25;
	public static final int LAB = 26;
	public static final int LAB_CODE = 27;
	public static final int LAB_NUMBER = 28;
	public static final int COLLECTION_COMMENTS = 29;
	public static final int TAXONOMIC_LIST = 30;

	protected static Pool pool = new Pool();
	protected int id;
	private Object[] values = new Object[31];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getAdoptionRecord method instead.
	 */
	protected PaleontologyRecord(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, FEATURE_SECURITY_CLASS_ID, AUDIT_ID, "
				+ "STATUS, SECURITY_CLASS_ID, SAMPLE_NAME, DRILLHOLE_DEPTH, IDENTIFICATION_DATE, DATE_ROUNDING, "
				+ "STAGE_ID, STAGE, STAGE_ABBREV, STAGE_LOWER_ID, STAGE_LOWER, STAGE_LOWER_MOD, STAGE_UPPER_ID, "
				+ "STAGE_UPPER, STAGE_UPPER_MOD, AGE_START, AGE_STOP, STAGE_COMMENTS, LAB_SECTION_ID, LAB, "
				+ "LAB_CODE, LAB_NUMBER, COLLECTION_COMMENTS "
				+ "FROM Paleontology_All_View WHERE Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = new Integer(rs.getInt(2));
			values[2] = new Integer(rs.getInt(3));
			values[3] = rs.getString(4);
			values[4] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[5] =
				((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[6] = rs.getString(7);
			values[7] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getString(10);
			values[11] = rs.getDate(11);
			values[12] = rs.getString(12);
			values[13] =
				((rs.getString(13) != null)
					? new Integer(rs.getInt(13))
					: null);
			values[14] = rs.getString(14);
			values[15] = rs.getString(15);
			values[16] =
				((rs.getString(16) != null)
					? new Integer(rs.getInt(16))
					: null);
			values[17] = rs.getString(17);
			values[18] = rs.getString(18);
			values[19] =
				((rs.getString(19) != null)
					? new Integer(rs.getInt(19))
					: null);
			values[20] = rs.getString(20);
			values[21] = rs.getString(21);
			values[22] =
				((rs.getString(22) != null)
					? new Double(rs.getDouble(22))
					: null);
			values[23] =
				((rs.getString(23) != null)
					? new Double(rs.getDouble(23))
					: null);
			values[24] = rs.getString(24);
			values[25] =
				((rs.getString(25) != null)
					? new Integer(rs.getInt(25))
					: null);
			values[26] = rs.getString(26);
			values[27] = rs.getString(27);
			values[28] = rs.getString(28);
			values[29] = rs.getString(29);
			rs.close();

			query =
				"SELECT Person_ID, Name FROM Person_View NATURAL JOIN Identifier WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector identVec = new Vector();
			while (rs.next()) {
				identVec.add(
					new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			values[10] = ((identVec.size() > 0) ? identVec : null);
			rs.close();

			query =
				"SELECT DISTINCT L.Name,P.Group_ID FROM Pal_List P, Lookup L WHERE P.Group_ID = L.Lookup_ID AND P.Record_ID = ? ORDER BY P.Group_ID";
			rs = conn.executeQuery(query, types, data);
			Statement preserveStatement = conn.preservePreparedStatement();
			Vector taxaGroupVec = new Vector();
			while (rs.next()) {
				TaxaGroup taxaGroup = new TaxaGroup(rs.getString(1));
				taxaGroup.setGroupId(
					((rs.getString(2) != null)
						? new Integer(rs.getInt(2))
						: null));
				query =
					"SELECT P.Taxonomic_Name, P.Taxa_ID, T.Author, P.Specimen_Count, P.Specimen_Coords, P.Comments FROM Pal_List P, Taxonomic_Lookup T WHERE P.Taxa_ID = T.Taxa_ID AND Record_ID = ? AND T.Group_ID = "
						+ taxaGroup.getGroupId()
						+ " ORDER BY P.Taxonomic_Name";
				ResultSet rs2 = conn.executeQuery(query, types, data);
				Vector taxaVec = new Vector();
				while (rs2.next()) {
					Taxa taxa = new Taxa(rs2.getString(1));
					taxa.setTaxaId(
						((rs2.getString(2) != null)
							? new Integer(rs2.getInt(2))
							: null));
					taxa.setAuthor(rs2.getString(3));
					taxa.setSpecimenCount(
						((rs2.getString(4) != null)
							? new Integer(rs2.getInt(4))
							: null));
					taxa.setSpecimenCoords(rs2.getString(5));
					taxa.setComments(rs2.getString(6));
					taxaVec.add(taxa);
				}
				if (taxaVec.size() > 0) {
					taxaGroup.setTaxaList(taxaVec);
				}
				rs2.close();
				taxaGroupVec.add(taxaGroup);
			}
			values[30] = ((taxaGroupVec.size() > 0) ? taxaGroupVec : null);
			rs.close();
			preserveStatement.close();

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
	public int getAsInt(int field) throws IllegalArgumentException {
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
	public double getAsDouble(int field) throws IllegalArgumentException {
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
	public java.util.Date getAsDate(int field) throws IllegalArgumentException {
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
	public String getAsString(int field) throws IllegalArgumentException {
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
	public Object get(int field) throws IllegalArgumentException {
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
	public static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (
				o instanceof PaleontologyRecord
					&& ((PaleontologyRecord) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		pool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static PaleontologyRecord getData(int id, User user, PageState state) throws SQLException, IOException, AccessDeniedException {
		PaleontologyRecord p = (PaleontologyRecord) pool.retrieve(new DataFinder(id));
		if (p == null) {
			p = new PaleontologyRecord(id, state);
		}
		if (!FREDUtils.isAllowedLocality(user, p.getAsString(FEATURE_SECURITY_CLASS_ID), p.getAsString(FEATURE_STATUS), p.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, p.getAsString(SECURITY_CLASS_ID), p.getAsString(STATUS), p.getAsString(RECORD_ID), state)) {
			throw new AccessDeniedException();
		}
		return p;
	}

	public String toString() {
		return (values[0]).toString();
	}
	
	//public void finalize() throws Throwable {
	//	pool.removeMe(this);
	//}

}
