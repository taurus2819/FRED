package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
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
			values[ADOPTION_DATE] = rs.getDate(1);
			values[ADOPTION_DATE_ROUNDING] = rs.getString(2);
			values[ADOPTED_STAGE_ID] = ((rs.getString(3) != null) ? new Integer(rs.getInt(3)) : null);
			values[ADOPTED_STAGE] = rs.getString(4);
			values[ADOPTED_STAGE_ABBREV] = rs.getString(5);
			values[ADOPTED_STAGE_LOWER_ID] = ((rs.getString(6) != null) ? new Integer(rs.getInt(6))	: null);
			values[ADOPTED_STAGE_LOWER] = rs.getString(7);
			values[ADOPTED_STAGE_LOWER_MOD] = rs.getString(8);
			values[ADOPTED_STAGE_UPPER_ID] = ((rs.getString(9) != null) ? new Integer(rs.getInt(9)) : null);
			values[ADOPTED_STAGE_UPPER] = rs.getString(10);
			values[ADOPTED_STAGE_UPPER_MOD] = rs.getString(11);
			values[ADOPTED_AGE_START] = ((rs.getString(12) != null) ? new Double(rs.getDouble(12)) : null);
			values[ADOPTED_AGE_STOP] = ((rs.getString(13) != null) ? new Double(rs.getDouble(13)) : null);
			values[COMMENTS] = rs.getString(14);
			rs.close();

			query = "SELECT Person_ID, Name FROM Person_View NATURAL JOIN Adoptor WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector adoVec = new Vector();
			while (rs.next()) {
				adoVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			rs.close();
			values[ADOPTOR] = ((adoVec.size() > 0) ? adoVec : null);
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
	public static Record getData(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException, InvalidCredentialsException {
		Record rec = (AdoptionRecord) pool.retrieve(new DataFinder(id));
		if (forceRefresh && rec != null) {
			pool.removeMe(rec);
			rec = null;
		}
		if (rec == null) {
			rec = new AdoptionRecord(id, state);
		} else {
		}
		if (!FREDUtils.isAllowedLocality(user, rec.getAsString(FEATURE_STATUS), rec.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedSample(user, rec.getAsString(SAMPLE_SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(SAMPLE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, rec.getAsString(SECURITY_CLASS_ID), rec.getAsString(STATUS), rec.getAsString(RECORD_ID), state))
			throw new InvalidCredentialsException();
		return rec;
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static Record getData(int id, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		return getData(id, user, state, false);
	}

}
