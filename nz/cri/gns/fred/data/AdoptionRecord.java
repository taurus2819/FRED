package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Adoption_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getData method instead.
 */
public class AdoptionRecord extends Record {

	public static final int ADOPTOR = 10;
	public static final int ADOPTION_DATE = 11;
	public static final int DATE_ROUNDING = 12;
	public static final int ADOPTED_STAGE_ID = 13;
	public static final int ADOPTED_STAGE = 14;
	public static final int ADOPTED_STAGE_ABBREV = 15;
	public static final int ADOPTED_STAGE_LOWER_ID = 16;
	public static final int ADOPTED_STAGE_LOWER = 17;
	public static final int ADOPTED_STAGE_LOWER_MOD = 18;
	public static final int ADOPTED_STAGE_UPPER_ID = 19;
	public static final int ADOPTED_STAGE_UPPER = 20;
	public static final int ADOPTED_STAGE_UPPER_MOD = 21;
	public static final int ADOPTED_AGE_START = 22;
	public static final int ADOPTED_AGE_STOP = 23;
	public static final int COMMENTS = 24;

	/**
	 * Cannot be called directly. use static getAdoptionRecord method instead.
	 */
	private AdoptionRecord(int id, PageState state)
		throws SQLException, IOException {
		super(id, state);
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT ADOPTION_DATE, DATE_ROUNDING, "
				+ "ADOPTED_STAGE_ID, ADOPTED_STAGE, ADOPTED_STAGE_ABBREV, ADOPTED_STAGE_LOWER_ID, "
				+ "ADOPTED_STAGE_LOWER, ADOPTED_STAGE_LOWER_MOD, ADOPTED_STAGE_UPPER_ID, ADOPTED_STAGE_UPPER, "
				+ "ADOPTED_STAGE_UPPER_MOD, ADOPTED_AGE_START, ADOPTED_AGE_STOP, COMMENTS "
				+ "FROM Adoption_All_View WHERE Record_ID = ?";
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
			rs.close();

			query =
				"SELECT Person_ID, Name FROM Person_View NATURAL JOIN Adoptor WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector adoVec = new Vector();
			while (rs.next()) {
				adoVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			rs.close();
			values[10] = ((adoVec.size() > 0) ? adoVec : null);
			rs.close();

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
				o instanceof AdoptionRecord
					&& ((AdoptionRecord) o).id == this.id);
		}

	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static AdoptionRecord getAdoptionData(int id, User user, PageState state) throws SQLException, IOException, AccessDeniedException {
		AdoptionRecord a = (AdoptionRecord) pool.retrieve(new DataFinder(id));
		if (a == null) {
			a = new AdoptionRecord(id, state);
		}
		if (!FREDUtils.isAllowedLocality(user, a.getAsString(FEATURE_SECURITY_CLASS_ID), a.getAsString(FEATURE_STATUS), a.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, a.getAsString(SECURITY_CLASS_ID), a.getAsString(STATUS), a.getAsString(RECORD_ID), state)) {
			throw new AccessDeniedException();
		}
		return a;
	}

}
