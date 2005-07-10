package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.metadata.MetadataRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Feature record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class Feature implements FeatureConstants {

	private FeatureData fd;
	private PageState state;
	private User user;
	private boolean authenticated;

	public Feature(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.state = state;
		this.user = user;
		this.fd = FeatureData.getData(id, state, forceRefresh);
		if (FREDUtils.isAllowedLocality(user, fd.getAsString(STATUS), fd.getAsString(FEATURE_ID), state)) {
			authenticated = true;
		} else {
			authenticated = false;
		}		
	}

	public Feature(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getFeatureID() {
		return fd.getAsInt(FEATURE_ID);
	}

	public String getFeatureType() {
		return fd.getAsString(FEATURE_TYPE);
	}

	public boolean isUserAuthenticated() {
		return authenticated;
	}
	
	public boolean isApprovedLocality() {
		return (fd.getAsString(STATUS).equals(Audit.STATUS_APPROVED));
	}

	public int getSampleCount() {
		return fd.getAsVector(SAMPLES).size();
	}

	public MetadataRecord[] getMetadataRecords() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		return fd.mr;
	}
	
	public int getMetadataRecordsCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		if (fd.mr != null)
			return fd.mr.length;
		return 0;
	}		
	
	private boolean isAllowedField(int field) {
		if (authenticated)
			return true;
		if (field == FEATURE_TYPE || field == STATUS)
			return true;
		if (isApprovedLocality()) {
			switch (field) {
				case FEATURE_ID :
				case FEATURE_NAME :
				case SAMPLE_NAMES :
				case MASTERFILE_ID :
				case MASTERFILE_NAME :
				case AUDIT_ID :
				case SECURITY_CLASS_ID :
				case STATUS :
				case SITE_ID :
				case SAMPLES :
				case PETWELL_LINK :
				return true;
			}		
		}
		return false;
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return fd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 *
	public static int getPoolSize() {
		return FeatureData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 *
	public static void purge() {
		FeatureData.purge();
	}
*/
	public String toString() {
		return fd.toString();
	}

	public int addNewSample(String topDepth, String bottomDepth, String drillTypeID, String workingFolderID) throws DataInputException, IOException, SQLException {
		if (bottomDepth.equals(""))
			bottomDepth  = null;
		if (drillTypeID.equals(""))
			drillTypeID = null;
		if (!FREDUtils.isNumeric(topDepth) || (bottomDepth != null && !FREDUtils.isNumeric(bottomDepth)) || (drillTypeID != null && !FREDUtils.isNumeric(drillTypeID)))
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		DBConnection conn = FREDUtils.getFREDConnection(state);
		conn.getConnection().setAutoCommit(false);
		int sampleID;
		try {
			//Add new AUDIT and SAMPLE records	
			QueryDescriptor qd = new QueryDescriptor("audit_table");
			qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
			qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
			qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
			qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(workingFolderID));
			String auditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
			ResultSet rs = conn.executeQuery("SELECT MIN(fr_id) FROM sample WHERE feature_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(getFeatureID())});
			rs.next();
			qd = new QueryDescriptor("sample");
			qd.addQueryColumn("feature_id", Types.NUMERIC, new Integer(getFeatureID()));
			qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(auditID));
			if (rs.getString(1) != null)
				qd.addQueryColumn("fr_id", Types.NUMERIC, new Integer(rs.getInt(1)));
			qd.addQueryColumn("top_depth", Types.NUMERIC, new Double(topDepth));
			if (bottomDepth != null)
				qd.addQueryColumn("bottom_depth", Types.NUMERIC, new Double(bottomDepth));
			if (drillTypeID != null)
				qd.addQueryColumn("drill_type_id", Types.NUMERIC, new Integer(drillTypeID));
			sampleID = Integer.parseInt(DBUtils.doInsertUsingSequence(qd, "sample_id", "sample_seq", conn, true));
			//Delete the default sample - if there is one.
			String query = "SELECT s.sample_id FROM sample s, record r WHERE s.sample_id = r.sample_id(+) AND s.feature_id = ? AND s.top_depth IS NULL AND s.bottom_depth IS NULL AND s.drill_type_id IS NULL AND r.sample_id IS NULL";
			rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(getFeatureID())});
			if (rs.next())
				conn.executeUpdate("DELETE FROM sample WHERE sample_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(rs.getInt(1))});

			conn.getConnection().commit();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
		} catch (SQLException e) {
			conn.getConnection().rollback();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
			throw e;
		}
		fd = FeatureData.getData(getFeatureID(), state, true);
		return sampleID;
	}

}
