package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Paleontology_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class PaleontologyRecord extends Record {

	private static Pool pool = new Pool();

	/**
	 * Cannot be called directly. use static getPaleontologyRecord method instead.
	 */
	private PaleontologyRecord(int id, PageState state) throws SQLException, IOException {
		super(id, state);
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query = "SELECT IDENTIFICATION_DATE, DATE_ROUNDING, "
				+ "STAGE_ID, STAGE, STAGE_ABBREV, STAGE_LOWER_ID, STAGE_LOWER, STAGE_LOWER_MOD, STAGE_UPPER_ID, "
				+ "STAGE_UPPER, STAGE_UPPER_MOD, AGE_START, AGE_STOP, STAGE_COMMENTS, LAB_SECTION_ID, LAB, "
				+ "LAB_CODE, LAB_NUMBER, COLLECTION_COMMENTS "
				+ "FROM Paleontology_All_View WHERE Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next())
				throw new SQLException("Cannot find record in database with this id: " + this.id);
			values[IDENTIFICATION_DATE] = rs.getDate(1);
			values[IDENTIFICATION_DATE_ROUNDING] = rs.getString(2);
			values[STAGE_ID] = ((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[STAGE] = rs.getString(4);
			values[STAGE_ABBREV] = rs.getString(5);
			values[STAGE_LOWER_ID] = ((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[STAGE_LOWER] = rs.getString(7);
			values[STAGE_LOWER_MOD] = rs.getString(8);
			values[STAGE_UPPER_ID] = ((rs.getString(9) != null) ? new Integer(rs.getInt(9)) : null);
			values[STAGE_UPPER] = rs.getString(10);
			values[STAGE_UPPER_MOD] = rs.getString(11);
			values[AGE_START] = ((rs.getString(12) != null) ? new Double(rs.getDouble(12)) : null);
			values[AGE_STOP] = ((rs.getString(13) != null) ? new Double(rs.getDouble(13)) : null);
			values[STAGE_COMMENTS] = rs.getString(14);
			values[LAB_SECTION_ID] = ((rs.getString(15) != null) ? new Integer(rs.getInt(15)) : null);
			values[LAB] = rs.getString(16);
			values[LAB_CODE] = rs.getString(17);
			values[LAB_NUMBER] = rs.getString(18);
			values[COLLECTION_COMMENTS] = rs.getString(19);
			rs.close();
			query = "SELECT Person_ID, Name FROM Person_View NATURAL JOIN Identifier WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector identVec = new Vector();
			while (rs.next()) {
				identVec.add(
					new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			values[IDENTIFIER] = ((identVec.size() > 0) ? identVec : null);
			rs.close();
			int provTaxaCount = 0;
			query = "SELECT DISTINCT L.Name,P.Group_ID FROM Pal_List P, Lookup L WHERE P.Group_ID = L.Lookup_ID AND P.Record_ID = ? ORDER BY UPPER(L.Name)";
			rs = conn.executeQuery(query, types, data);
			Statement preserveStatement = conn.preservePreparedStatement();
			Vector taxaGroupVec = new Vector();
			while (rs.next()) {
				TaxaGroup taxaGroup = new TaxaGroup(rs.getString(1));
				taxaGroup.setGroupID(((rs.getString(2) != null)	? new Integer(rs.getInt(2))	: null));
				query =	"SELECT p.taxonomic_name, p.taxa_id, t.author, p.specimen_count, p.specimen_coords, p.comments, t.status "
						+ "FROM pal_list p, taxonomic_lookup t WHERE p.taxa_id = t.taxa_id AND p.record_id = ? AND p.group_id = ?"
						+ " ORDER BY UPPER(p.taxonomic_name)";
				ResultSet rs2 = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(this.id), taxaGroup.getGroupID()});
				Vector taxaVec = new Vector();
				while (rs2.next()) {
					Taxa taxa = new Taxa(rs2.getString(1));
					taxa.setTaxaID(((rs2.getString(2) != null) ? new Integer(rs2.getInt(2))	: null));
					taxa.setAuthor(rs2.getString(3));
					taxa.setSpecimenCount(((rs2.getString(4) != null) ? new Integer(rs2.getInt(4)) : null));
					taxa.setSpecimenCoords(rs2.getString(5));
					taxa.setComments(rs2.getString(6));
					taxa.setStatus(rs2.getString(7));
					if (rs2.getString(7).equals("provisional"))
						provTaxaCount++;
					taxaVec.add(taxa);
				}
				if (taxaVec.size() > 0) {
					taxaGroup.setTaxaList(taxaVec);
				}
				rs2.close();
				taxaGroupVec.add(taxaGroup);
			}
			values[TAXONOMIC_LIST] = ((taxaGroupVec.size() > 0) ? taxaGroupVec : null);
			values[PROVISIONAL_TAXA_COUNT] = new Integer(provTaxaCount);
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
			return (o instanceof PaleontologyRecord	&& ((PaleontologyRecord) o).id == this.id);
		}
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not a record for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException, InvalidCredentialsException {
		Record rec = (PaleontologyRecord) pool.retrieve(new DataFinder(id));
		if (forceRefresh && rec != null) {
			pool.removeMe(rec); 
			rec = null;
		}
		if (rec == null)
			rec = new PaleontologyRecord(id, state);
		if (!FREDUtils.isAllowedLocality(user, rec.getAsString(FEATURE_STATUS), rec.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedSample(user, rec.getAsString(SAMPLE_SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(SAMPLE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, rec.getAsString(SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(RECORD_ID), state))
			throw new InvalidCredentialsException();
		return rec;
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not a record for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		return getData(id, user, state, false);
	}

}
