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

	public static DBConnection getFREDConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(state.getSession(), CONNECTION, DB_NAME, state.getContext());
	}

	public static DBConnection getIPConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(state.getSession(), IP_CONNECTION, IP_DB_NAME, state.getContext());
	}

	/**
	 * Returns true if this locality can be viewed by the user
	 */
	public static boolean isAllowedLocality(User user, String securityClassID, String status, String featureID, PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals(Audit.STATUS_APPROVED))
			return (getUserWorkingLocalityRights(user, featureID, state) & 1) > 0;
	//	if (securityClassID != null)
	//		return hasMasterfileRights(user, featID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		return true;
	}

	/**
	 * Returns true if this sample can be viewed by the user
	 */
	public static boolean isAllowedSample(User user, String securityClassID, String status, String sampleID, PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals(Audit.STATUS_APPROVED))
			return (getUserWorkingSampleRights(user, sampleID, state) & 1) > 0;
		if (securityClassID != null)
			return hasMasterfileSampleRights(user, sampleID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		return true;
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
		String query = "SELECT masterfile_id FROM feature WHERE feature_id = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(featureID)});
		if (rs.next()) {
			query = "SELECT user_rights FROM folder_view WHERE user_id = ? AND folder_id = ?";
			rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(user.getPersonId()), new Integer(rs.getInt(1))});
			if (rs.next())
				userRights = rs.getInt(1);
		}
		return (userRights & 1) > 0;
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
			return (getUserWorkingLocalityRights(user, featureID, state) & 64) > 0;
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
	
	//TODO use NZMS260 method instead
	private static final String validMapSheets = "A44A45B41B42B43B44B45B46B47C40C41C42C43C44C45C46C49C50D38D39D40D41D42D43D44D45D46"
		+ "D47D48D49D50E37E38E39E40E41E42E43E44E45E46E47E48E49F36F37F38F39F40F41F42F43F44F45F46F47F48G35G36G37G38G39G40G41G42G43G44"
		+ "G45G46G47H34H35H36H37H38H39H40H41H42H43H44H45H46H47I33I34I35I36I37I38I39I40I41I42I43I44I45J31J32J33J34J35J36J37J38J39J40"
		+ "J41J42J43J44K29K30K31K32K33K34K35K36K37K38K39L01L25L26L27L28L29L30L31L32L33L34L35L36L37M02M24M25M26M27M28M29M30M31M32M33"
		+ "M34M35M36M37N02N03N04N05N24N25N26N27N28N29N30N31N32N33N34N36N37O03O04O05O06O07O26O27O28O29O30O31O32O33P04P05P06P07P08P09"
		+ "P19P20P21P25P26P27P28P29P30P31Q04Q05Q06Q07Q08Q09Q10Q11Q12Q15Q18Q19Q20Q21Q22Q26Q27Q29R06R07R08R09R10R11R12R13R14R15R16R17"
		+ "R18R19R20R21R22R23R25R26R27R28S07S08S09S10S11S12S13S14S15S16S17S18S19S20S21S22S23S24S25S26S27S28T08T09T10T11T12T13T14T15"
		+ "T16T17T18T19T20T21T22T23T24T25T26T27T28U10U11U12U13U14U15U16U17U18U19U20U21U22U23U24U25U26V14V15V16V17V18V19V20V21V22V23"
		+ "V24W13W14W15W16W17W18W19W20W21W22X14X15X16X17X18X19X20Y14Y15Y16Y17Y18Y19Y20Z14Z15Z16Z17";

	public static boolean isValidMapSheet(String mapSheet) {
		return (validMapSheets.indexOf(mapSheet) >= 0);
	}
}
