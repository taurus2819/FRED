package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
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
public class Feature {

	public static final int FEATURE_ID = 0;
	public static final int SITE_ID = 1;
	public static final int AUDIT_ID = 2;
	public static final int SECURITY_CLASS_ID = 20;
	public static final int STATUS = 23;
	public static final int CREATED_DATE = 25;
	public static final int MASTERFILE_ID = 3;
	public static final int MASTERFILE_NAME = 4;
	public static final int LOCALITY = 5;
	public static final int REG_AREA_ID = 6;
	public static final int COMMENTS = 7;
	public static final int FEATURE_TYPE = 8;
	public static final int FEATURE_NAME = 9;
	public static final int SAMPLE_NAMES = 24;
	public static final int DRILLHOLE_LICENCE_NAME = 10;
	public static final int START_DATE = 11;
	public static final int START_DATE_ROUNDING = 12;
	public static final int FINISH_DATE = 13;
	public static final int FINISH_DATE_ROUNDING = 14;
	public static final int PERSON_ID = 15;
	public static final int DATUM_TYPE = 16;
	public static final int DATUM_ELEVATION = 17;
	public static final int START_DEPTH = 18;
	public static final int FINISH_DEPTH = 19;
	public static final int SAMPLES = 21;
	public static final int PETWELL_LINK = 22;
	public static final int WORKING_FOLDER_ID = 26;

	public static final String OUTCROP_LOCALITY = "Outcrop";
	public static final String DRILLHOLE_LOCALITY = "Drillhole";
	public static final String VERTICAL_SECTION_LOCALITY = "Vertical Section";
	
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

	public MetadataRecord[] getMetadataRecords() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
		return fd.mr;
	}
	
	public int getMetadataRecordsCount() throws InvalidCredentialsException {
		if (!authenticated)
			throw new InvalidCredentialsException();
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
	public int getAsInt(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		if (!isAllowedField(field)) {
			throw new InvalidCredentialsException();
		}
		return fd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return FeatureData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		FeatureData.purge();
	}

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
			//check existing samples.  If there is only one - the default one - then delete it
			String query = "SELECT s.sample_id FROM sample s, record r WHERE s.sample_id = r.sample_id(+) AND s.feature_id = ? AND s.top_depth IS NULL AND s.bottom_depth IS NULL AND s.drill_type_id IS NULL AND r.sample_id IS NULL";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(getFeatureID())});
			if (rs.next())
				conn.executeUpdate("DELETE FROM sample WHERE sample_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(rs.getInt(1))});
			//Add new AUDIT and SAMPLE records	
			QueryDescriptor qd = new QueryDescriptor("audit_table");
			qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
			qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
			qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
			qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(workingFolderID));
			String auditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
			rs = conn.executeQuery("SELECT MIN(fr_id) FROM sample WHERE feature_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(getFeatureID())});
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
