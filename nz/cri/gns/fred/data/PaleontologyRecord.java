package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Paleontology_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class PaleontologyRecord extends Record {

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

	/**
	 * Cannot be called directly. use static getAdoptionRecord method instead.
	 */
	private PaleontologyRecord(int id, PageState state)
		throws SQLException, IOException {
		super(id, state);
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT IDENTIFICATION_DATE, DATE_ROUNDING, "
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
			values[11] = rs.getDate(1);
			values[12] = rs.getString(2);
			values[13] =
				((rs.getString(3) != null)
					? new Integer(rs.getInt(3))
					: null);
			values[14] = rs.getString(4);
			values[15] = rs.getString(5);
			values[16] =
				((rs.getString(6) != null)
					? new Integer(rs.getInt(6))
					: null);
			values[17] = rs.getString(7);
			values[18] = rs.getString(8);
			values[19] =
				((rs.getString(9) != null)
					? new Integer(rs.getInt(9))
					: null);
			values[20] = rs.getString(10);
			values[21] = rs.getString(11);
			values[22] =
				((rs.getString(12) != null)
					? new Double(rs.getDouble(12))
					: null);
			values[23] =
				((rs.getString(13) != null)
					? new Double(rs.getDouble(13))
					: null);
			values[24] = rs.getString(14);
			values[25] =
				((rs.getString(15) != null)
					? new Integer(rs.getInt(15))
					: null);
			values[26] = rs.getString(16);
			values[27] = rs.getString(17);
			values[28] = rs.getString(18);
			values[29] = rs.getString(19);
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
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static PaleontologyRecord getPaleontologyData(int id, User user, PageState state) throws SQLException, IOException, AccessDeniedException {
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

}
