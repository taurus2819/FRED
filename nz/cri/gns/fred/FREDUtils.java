package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.cri.gns.auth.AuthUtils;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.NoSuchSecurityClassException;
import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.Audit;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;

public class FREDUtils {

	public static final String CONNECTION = "nz.cri.gns.fr.connection";
	public static final String DB_NAME = "fr";
	public static final String IP_CONNECTION = "nz.cri.gns.ip.connection";
	public static final String IP_DB_NAME = "ip";
	
	public static final int REG_MAINLAND_NZ = 400;
	public static final int REG_CHATHAM_ISLANDS = 401;
	public static final int REG_ROSS_SEA = 402;
	public static final int REG_NEW_CALEDONIA = 403;
	public static final int REG_TOKELAU = 404;
	public static final int REG_FIJI = 405;
	public static final int REG_SAMOA = 406;
	public static final int REG_NIUE = 407;
	public static final int REG_COOK_ISLANDS = 408;
	public static final int REG_NORFOLK_ISLAND = 409;
	public static final int REG_TONGA = 410;
	public static final int REG_LORD_HOWE_ISLAND = 411;
	public static final int REG_KERMADEC_ISLANDS = 412;
	public static final int REG_BOUNTY_ISLANDS = 413;
	public static final int REG_THE_SNARES = 414;
	public static final int REG_CAMPBELL_ISLAND = 415;
	public static final int REG_AUCKLAND_ISLANDS = 416;
	public static final int REG_ANTIPODES_ISLANDS = 417;
	public static final int REG_MACQUARIE_ISLAND = 418;
	public static final int REG_OTHER = 419;

	public static final int MASTERFILE_NTH_NI = 1;
	public static final int MASTERFILE_CEN_NI = 2;
	public static final int MASTERFILE_STH_NI = 3;
	public static final int MASTERFILE_NELSON = 4;
	public static final int MASTERFILE_CEN_SI = 5;
	public static final int MASTERFILE_STH_SI = 6;
	public static final int MASTERFILE_NZ_ISLANDS = 7;
	public static final int MASTERFILE_ANTARCTICA = 8;
	public static final int MASTERFILE_PACIFIC_ISLANDS = 9;
	public static final int MASTERFILE_NEW_CALEDONIA = 10;
	public static final int MASTERFILE_OFFSHORE = 11;

	private static final int FRED_EDIT_SC = 15;
	
	public static DBConnection getFREDConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(state.getSession(), CONNECTION, DB_NAME, state.getContext());
	}

	public static DBConnection getIPConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(state.getSession(), IP_CONNECTION, IP_DB_NAME, state.getContext());
	}

	/**
	 * Returns true if this locality can be viewed by the user
	 */
	public static boolean isAllowedLocality(User user, String status, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || featureID == null)
			return false;
		if (!status.equals(Audit.STATUS_APPROVED))
			return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_READ_RIGHT) > 0;
		return true;
	}

	/**
	 * Returns true if the locality can be edited (and saved) by the user
	 */
	public static boolean isAllowedEditLocality(User user, String status, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || featureID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED))
			return (hasMasterfileRights(user, featureID, state) || checkSecurityClass(FRED_EDIT_SC, user, state));
		if (status.equals(Audit.STATUS_WAITING))
			return hasMasterfileRights(user, featureID, state);
		return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_EDIT_RIGHT) > 0;
	}

	/**
	 * Return true if the user has rights to delete the locality
	 */
	public static boolean isAllowedDeleteLocality(User user, String status, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || featureID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED))
			return false;		
		if (status.equals(Audit.STATUS_WAITING))
			return hasMasterfileRights(user, featureID, state);
		return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_DELETE_RIGHT) > 0;
	}
	
	/**
	 * Returns true if the locality can be submitted by the user
	 */
	public static boolean isAllowedSubmitLocality(User user, String status, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || featureID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED) || status.equals(Audit.STATUS_WAITING))
			return false;
		return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_SUBMIT_RIGHT) > 0;		
	}

	/**
	 * Returns true if the locality can be revoked by the user
	 */
	public static boolean isAllowedRevokeLocality(User user, String status, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || featureID == null)
			return false;
		if (status.equals(Audit.STATUS_WAITING))
			return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_SUBMIT_RIGHT) > 0;
		return false;
	}
	
	/**
	 * Returns true if this sample can be viewed by the user
	 */
	public static boolean isAllowedSample(User user, String securityClassID, String status, String sampleID, PageState state) throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals(Audit.STATUS_APPROVED))
			return (getUserWorkingSampleRights(user, sampleID, state) & Folder.FOLDER_READ_RIGHT) > 0;
		if (securityClassID != null)
			return hasMasterfileSampleRights(user, sampleID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		return true;
	}

	/**
	 * Returns true if the sample can be edited (and saved) by the user
	 */
	public static boolean isAllowedEditSample(User user, String status, String sampleID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || sampleID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED))
			return (hasMasterfileSampleRights(user, sampleID, state) || checkSecurityClass(FRED_EDIT_SC, user, state));
		if (status.equals(Audit.STATUS_WAITING))
			return hasMasterfileSampleRights(user, sampleID, state);
		return (getUserWorkingSampleRights(user, sampleID, state) & Folder.FOLDER_EDIT_RIGHT) > 0;
	}

	/**
	 * Return true if the user has rights to delete the sample
	 */
	public static boolean isAllowedDeleteSample(User user, String status, String sampleID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || sampleID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED))
			return false;		
		if (status.equals(Audit.STATUS_WAITING))
			return hasMasterfileSampleRights(user, sampleID, state);
		return (getUserWorkingSampleRights(user, sampleID, state) & Folder.FOLDER_DELETE_RIGHT) > 0;
	}
	
	/**
	 * Returns true if the sample can be submitted by the user
	 */
	public static boolean isAllowedSubmitSample(User user, String status, String sampleID, PageState state) throws IOException, SQLException {
		if (user == null || status == null || sampleID == null)
			return false;
		if (status.equals(Audit.STATUS_APPROVED))
			return false;	
		if (status.equals(Audit.STATUS_WAITING))
			return hasMasterfileSampleRights(user, sampleID, state);
		return (getUserWorkingSampleRights(user, sampleID, state) & Folder.FOLDER_SUBMIT_RIGHT) > 0;		
	}
	
	/**
	 * Returns true if this record can be viewed by the user
	 */
	public static boolean isAllowedRecord(User user, String securityClassID, String status, String recordID, PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals(Audit.STATUS_APPROVED))
			return (getUserWorkingRecordRights(user, recordID, state) & 1) > 0;
		if (securityClassID != null)
			return hasMasterfileRecordRights(user, recordID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		return true;
	}

	/**
	 * Returns true if the user has masterfile rights for this locality
	 */
	public static boolean hasMasterfileRights(User user, String featureID, PageState state) throws IOException, SQLException {
		if (user == null || featureID == null)
			return false;
		int userRights = 0;
		DBConnection conn = getFREDConnection(state);
		String query = "SELECT fv.user_rights FROM folder_view fv, feature f WHERE fv.user_id = ? AND fv.folder_id = f.masterfile_id AND f.feature_id = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(user.getPersonId()), new Integer(featureID)});
		if (rs.next())
			userRights = rs.getInt(1);
		return (userRights & Folder.FOLDER_READ_RIGHT) > 0;
	}

	/**
	 * Returns true is the user has masterfile rights for this sample (by checking rights for the locality
	 */
	public static boolean hasMasterfileSampleRights(User user, String sampleID, PageState state) throws IOException, SQLException {
		if (user == null || sampleID == null)
			return false;
		DBConnection conn = getFREDConnection(state);
		String query = "SELECT feature_id FROM sample WHERE sample_id = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(sampleID)});
		if (rs.next()) 
			return hasMasterfileRights(user, rs.getString(1), state);
		return false;		
	}

	/**
	 * Returns true is the user has masterfile rights for this record (by checking rights for the locality
	 */
	public static boolean hasMasterfileRecordRights(User user, String recordID, PageState state) throws IOException, SQLException {
		if (user == null || recordID == null || state == null)
			return false;
		DBConnection conn = getFREDConnection(state);
		String query = "SELECT feature_id FROM record_all_view WHERE record_id = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(recordID)});
		if (rs.next())
			return hasMasterfileRights(user, rs.getString(1), state);
		return false;
	}
	
	/**
	 * Returns true is the user is allowed to approve the locality
	 */
	public static boolean isAllowedApproveLocality(User user, String featureID, String status, PageState state) throws IOException, SQLException {
		if (status != null && status.equals(Audit.STATUS_WAITING))
			return (getUserWorkingLocalityRights(user, featureID, state) & Folder.FOLDER_APPROVE_RIGHT) > 0;
		return false;
	}

	private static boolean checkSecurityClass(int secClassID, User user, PageState state) throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc = new SecurityClass(secClassID, conn);
		SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
		return sca.isAccessibleTo(user, conn);		
	}

	/**
	 * Returns the rights the user_rights for the given locality
	 */
	public static int getUserWorkingLocalityRights(User user, String featureID, PageState state)
		throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = "SELECT DISTINCT user_rights FROM folder_content_short_view WHERE feature_id = ? AND user_id = ?";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(featureID), new Integer(user.getPersonId())});
			while (rs.next())
				userRights = userRights | rs.getInt(1);
		}
		return userRights;
	}

	/**
	 * Returns the rights the user_rights for the given sample
	 */
	public static int getUserWorkingSampleRights(User user, String sampleID, PageState state) throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = "SELECT fc.user_rights FROM folder_content_short_view fc, sample s WHERE fc.feature_id = s.feature_id AND s.sample_id = ? AND fc.user_id = ?";
			ResultSet rs =	conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), new Integer(user.getPersonId())});
			while (rs.next())
				userRights = userRights | rs.getInt(1);
		}
		return userRights;
	}

	/**
	 * Returns the rights the user_rights for the given record
	 */
	public static int getUserWorkingRecordRights(User user, String recordID, PageState state) throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = "SELECT fc.user_rights FROM folder_content_short_view fc, sample s, record r WHERE fc.feature_id = s.feature_id AND r.sample_id = s.sample_id AND r.record_id = ? AND fc.user_id = ?";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(user.getPersonId())}); 
			while (rs.next())
				userRights = userRights | rs.getInt(1);
		}
		return userRights;
	}

	/**
	 * Returns the rights the user_rights for the given folder
	 */
	public static int getUserFolderRights(User user,  String folderID, PageState state) throws IOException, SQLException {
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = "SELECT user_rights FROM folder_view WHERE folder_id = ? AND user_id = ?";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(folderID), new Integer(user.getPersonId())}); 
			if (rs.next())
				return rs.getInt(1);
		}
		return 0;

	}

	public static int getSecurityType(int secClassID, User user, PageState state) throws IOException, SQLException {
		if (secClassID == 4)
			return 21; //public
		int secType;
		DBConnection conn = getIPConnection(state);
		ResultSet rs = conn.executeQuery("SELECT COUNT(*) FROM user_right WHERE ur_sc_id = " + secClassID);
		rs.next();
		if (rs.getInt(1) == 0) {
			rs = conn.executeQuery("SELECT COUNT(*) FROM org_right WHERE or_sc_id = " + secClassID);
			rs.next();
			if (rs.getInt(1) == 1) {
				rs = conn.executeQuery("SELECT or_org_id FROM org_right WHERE or_sc_id = " + secClassID);
				rs.next();
				if (rs.getInt(1) == user.getOrgId()) {
					secType = 24;
				} else {
					throw new NoSuchSecurityClassException(secClassID);
				}
			} else {
				throw new NoSuchSecurityClassException(secClassID);
			}
		} else if (rs.getInt(1) == 1) {
			rs = conn.executeQuery("SELECT ur_person_id FROM user_right WHERE ur_sc_id = " + secClassID);
			rs.next();
			if (rs.getInt(1) == user.getPersonId()) {
				secType = 22;
			} else {
				throw new NoSuchSecurityClassException(secClassID);
			}
		} else {
			throw new NoSuchSecurityClassException(secClassID);
		}
		rs = conn.executeQuery("SELECT COUNT(*) FROM group_right WHERE gr_id = 1 AND gr_sc_id = " + secClassID);
		rs.next();
		if (rs.getInt(1) == 1)
			secType += 1;
		return secType;
	}

	public static int getSecurityClass(int secType, User user, PageState state) throws IOException, SQLException {
		int secClass = 0;
		DBConnection conn = getIPConnection(state);
		Statement statement = conn.getExtraStatement();
		ResultSet rs, rs2;
		switch (secType) {
			case 21 : //public
				secClass = 4;
				break;
			case 22 : //user
				rs = conn.executeQuery(
						"SELECT ur_sc_id FROM user_right, org_right, group_right WHERE ur_sc_id = or_sc_id(+) AND ur_sc_id = gr_sc_id(+) AND or_id IS NULL AND gr_id IS NULL AND ur_person_id = "
							+ user.getPersonId());
				while (rs.next()) {
					rs2 = statement.executeQuery("SELECT COUNT(*) FROM user_right WHERE ur_sc_id = " + rs.getString(1) + " AND ur_person_id <> " + user.getPersonId());
					rs2.next();
					if (rs2.getInt(1) == 0) {
						secClass = rs.getInt(1);
						break;
					}
				}
				if (secClass == 0)
					secClass = addUserRight(user, state, false);
				statement.close();
				break;
			case 23 : //user + paleo
				rs = conn.executeQuery(
						"SELECT ur_sc_id FROM user_right, org_right, group_right WHERE ur_sc_id = or_sc_id(+) AND ur_sc_id = gr_sc_id AND or_id IS NULL AND gr_id = 1 AND ur_person_id = "
							+ user.getPersonId());
				while (rs.next()) {
					rs2 = statement.executeQuery("SELECT COUNT(*) FROM user_right WHERE ur_sc_id = " + rs.getString(1) + " AND ur_person_id <> " + user.getPersonId());
					rs2.next();
					if (rs2.getInt(1) == 0) {
						secClass = rs.getInt(1);
						break;
					}
				}
				if (secClass == 0)
					secClass = addUserRight(user, state, true);
				statement.close();
				break;
			case 24 : //org
				rs = conn.executeQuery(
						"SELECT or_sc_id FROM org_right, user_right, group_right WHERE or_sc_id = ur_sc_id(+) AND or_sc_id = gr_sc_id(+) AND ur_id IS NULL AND gr_id IS NULL AND or_org_id = "
							+ user.getOrgId());
				while (rs.next()) {
					rs2 = statement.executeQuery("SELECT COUNT(*) FROM org_right WHERE or_sc_id = " + rs.getString(1) + " AND or_org_id <> " + user.getOrgId());
					rs2.next();
					if (rs2.getInt(1) == 0) {
						secClass = rs.getInt(1);
						break;
					}
				}
				if (secClass == 0)
					secClass = addOrgRight(user, state, false);
				statement.close();
				break;
			case 25 : // org + paleo
				rs = conn.executeQuery(
						"SELECT or_sc_id FROM org_right, user_right, group_right WHERE or_sc_id = ur_sc_id(+) AND or_sc_id = gr_sc_id AND ur_id IS NULL AND gr_id = 1 AND or_org_id = "
							+ user.getOrgId());
				while (rs.next()) {
					rs2 = statement.executeQuery("SELECT COUNT(*) FROM org_right WHERE or_sc_id = " + rs.getString(1) + " AND or_org_id <> " + user.getOrgId());
					rs2.next();
					if (rs2.getInt(1) == 0) {
						secClass = rs.getInt(1);
						break;
					}
				}
				if (secClass == 0)
					secClass = addOrgRight(user, state, true);
				statement.close();
				break;
		}
		return secClass;
	}

	public static boolean isTaxaPanelMember(User user, String groupID, PageState state) throws IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "SELECT * FROM taxa_panel WHERE group_id = ? AND panelist_id = ?";
		try {
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC},
				 new Object[] {new Integer(user.getPersonId()), new Integer(groupID)});
			rs.next();
			return true;
		} catch (Exception e) {}
		return false;		
	}

	/**
	 * Returns the Sample immediately above the given Sample in a drillhole or vertical section
	 */
	public static Sample getSampleAbove(Sample sample, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "SELECT sample_id FROM sample_all_view WHERE feature_id = ? AND top_depth < ? ORDER BY top_depth DESC";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sample.getAsInt(Sample.FEATURE_ID)), new Double(sample.getAsDouble(Sample.TOP_DEPTH))});
		if (rs.next())
			return new Sample(rs.getInt(1), user, state);
		return null;
	}

	/**
	 * Returns the Sample immediately below the given Sample in a drillhole or vertical section
	 */
	public static Sample getSampleBelow(Sample sample, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "SELECT sample_id FROM sample_all_view WHERE feature_id = ? AND top_depth > ? ORDER BY top_depth";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sample.getAsInt(Sample.FEATURE_ID)), new Double(sample.getAsDouble(Sample.TOP_DEPTH))});
		if (rs.next())
			return new Sample(rs.getInt(1), user, state);
		return null;
	}
	
	/**
	 * Returns the Masterfile for a given locality
	 */
	public static int getMasterfile(int regAreaID, Datum.LatLong latLong) {
		switch (regAreaID) {
			case REG_MAINLAND_NZ :
				try {
					Datum.Coordinate nzms260Coord = new NZMS260().convertFromNZGD49(latLong);
				} catch (Exception e) {
					return MASTERFILE_OFFSHORE;
				}
				Datum.Coordinate nzmgCoord = new NZMG().convertFromNZGD49(latLong);
				double easting = nzmgCoord.getEastWest();
				double northing = nzmgCoord.getNorthSouth();
				if (easting <= 2810000 && northing >= 6250000)
					return MASTERFILE_NTH_NI;
				if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000))
					return MASTERFILE_CEN_NI;
			  	if (easting >= 2650000)
			  		return 	MASTERFILE_STH_NI;
			  	if (northing >= 5920000)
			  		return MASTERFILE_NELSON;
				if (easting >= 2210000 && northing >= 5620000)
					return MASTERFILE_CEN_SI;
				return MASTERFILE_STH_SI;
			case REG_CHATHAM_ISLANDS :
			case REG_CAMPBELL_ISLAND :
			case REG_AUCKLAND_ISLANDS :
			case REG_ANTIPODES_ISLANDS :
			case REG_THE_SNARES :
				return MASTERFILE_NZ_ISLANDS;
			case REG_ROSS_SEA :
	  			return MASTERFILE_ANTARCTICA;
	  		case REG_TOKELAU :
	  		case REG_FIJI :
	  		case REG_SAMOA :
	  		case REG_NIUE :
	  		case REG_COOK_ISLANDS :
	  		case REG_NORFOLK_ISLAND :
	   		case REG_TONGA :
	   		case REG_LORD_HOWE_ISLAND :
	   		case REG_KERMADEC_ISLANDS :
	   		case REG_BOUNTY_ISLANDS :
	   		case REG_MACQUARIE_ISLAND :
	   			return MASTERFILE_PACIFIC_ISLANDS;
			case REG_NEW_CALEDONIA :
				return MASTERFILE_NEW_CALEDONIA;
			case REG_OTHER :
				return MASTERFILE_OFFSHORE;
		}
		return MASTERFILE_OFFSHORE;
	}

	private static int addUserRight(User user, PageState state, boolean paleo) throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc = AuthUtils.addSecurityClass("FR", user, conn,	"FRED Private User Class");
		sc.addUserToClass(user, new Right(1), conn);
		if (paleo)
			sc.addGroupToClass(1, new Right(1), conn);
		return sc.getId();
	}

	private static int addOrgRight(User user, PageState state, boolean paleo) throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc = AuthUtils.addSecurityClass("FR", user, conn, "FRED Private Org Class");
		sc.addUsersOrgToClass(user, new Right(1), conn);
		if (paleo)
			sc.addGroupToClass(1, new Right(1), conn);
		return sc.getId();
	}

	/**
	 * Returns a string of a date with appropriate formatting 
	 */
	public static String formatDateForOutput(Date date, String rounding) {
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy");
		if (rounding == null) {
			return DateFormat.getDateInstance(DateFormat.LONG).format(date);
		} else if (rounding.equals("Year")) {
			return yearFormatter.format(date);
		} else if (rounding.equals("Month")) {
			return monthFormatter.format(date);
		} else {
			return DateFormat.getDateInstance(DateFormat.LONG).format(date);
		}
	}

	/**
	 * Returns a string of a date with DateFormat.LONG formatting 
	 */
	public static String formatDateForOutput(Date date) {
		return formatDateForOutput(date, null);
	}

	/**
	 * Returns a string of a lat/long formatted for output
	 */
	public static String formatLatLongForOutput(double latitude, double longitude) {
		Datum.LatLong latlong = new Datum.LatLong(latitude, longitude);
		return latlong.getLatAsDegMinSec(2) + "|" + latlong.getLongAsDegMinSec(2);
	}

	/**
	 * Returns a string of the current date formatted correctly for DBUtils methods
	 */
	public static String getNowForSQL() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String noNulls(String in) {
		return (in == null || in.equals("null")) ? "" : in;
	}

	public static String noNulls(Integer in) {
		return (in == null) ? "" : in.toString();
	}

	public static String noNulls(Double in) {
		return (in == null) ? "" : in.toString();
	}

	public static String makeNulls(String in) {
		return (in == null || in.length() == 0) ? null : "'" + in + "'";
	}

	public static String makeDropDownNulls(String in) {
		return (in == null || in.equals("-")) ? null : "'" + in + "'";
	}
	
}
