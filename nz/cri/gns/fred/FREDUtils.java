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
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class FREDUtils {

	public static final String CONNECTION = "nz.cri.gns.fr.connection";
	public static final String DB_NAME = "fr";
	public static final String IP_CONNECTION = "nz.cri.gns.ip.connection";
	public static final String IP_DB_NAME = "ip";

	public static DBConnection getFREDConnection(PageState state)
		throws IOException {
		return JspUtils.createDatabaseConnection(
			state.getSession(),
			CONNECTION,
			DB_NAME,
			state.getContext());
	}

	public static DBConnection getIPConnection(PageState state)
		throws IOException {
		return JspUtils.createDatabaseConnection(
			state.getSession(),
			IP_CONNECTION,
			IP_DB_NAME,
			state.getContext());
	}

	public static boolean isAllowedLocality(User user, String securityClassID, String status, String featID, PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals("approved"))
			return (getUserWorkingLocalityRights(user, featID, state) & 1) > 0;
		if (securityClassID != null) {
			return hasMasterfileRights(user, featID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		} else {
			return true;
		}
	}

	public static boolean isAllowedRecord(User user, String securityClassID, String status, String recID, PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals("approved"))
			return (getUserWorkingRecordRights(user, recID, state) & 1) > 0;
		if (securityClassID != null) {
			return hasMasterfileRecordRights(user, recID, state) || checkSecurityClass(Integer.parseInt(securityClassID), user, state);
		} else {
			return true;
		}
	}

	public static boolean hasMasterfileRights(User user, String featID, PageState state) throws IOException, SQLException {
		if (user == null || featID == null || state == null)
			return false;
		int userRights = 0;
		DBConnection conn = getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Masterfile_ID FROM Feature WHERE Feature_ID = " + featID);
		if (rs.next()) {
			rs = conn.executeQuery("SELECT User_Rights FROM Folder_View WHERE User_ID = " + user.getPersonId() + " AND Folder_ID = " + rs.getString(1));
			if (rs.next())
				userRights = rs.getInt(1);
		}
		return (userRights & 1) > 0;
	}

	public static boolean hasMasterfileRecordRights(User user, String recID, PageState state) throws IOException, SQLException {
		if (user == null || recID == null || state == null)
			return false;
		DBConnection conn = getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Feature_ID FROM Record_All_View WHERE Record_ID = " + recID);
		if (rs.next()) {
			return hasMasterfileRights(user, rs.getString(1), state);
		} else {
			return false;
		}
	}

	public static boolean isAllowedApproveLocality(User user, String featID, String status, PageState state) throws IOException, SQLException {
		if (status != null && status.equals("waiting")) {
			return (getUserWorkingLocalityRights(user, featID, state) & 64) > 0;
		} else {
			return false;
		}
	}

	private static boolean checkSecurityClass(int secClassID, User user, PageState state) throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc =
			new SecurityClass(secClassID, conn);
		SecurityClassAccess sca =
			new SecurityClassAccess(sc, Right.ANY_RIGHT);
		return sca.isAccessibleTo(user, conn);		
	}

	public static int getUserWorkingLocalityRights(User user, String featID, PageState state)
		throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs =
				conn.executeQuery(
					"SELECT DISTINCT User_Rights FROM Folder_Content_Short_View WHERE Feature_ID = "
						+ featID
						+ " AND User_ID = "
						+ userID);
			while (rs.next()) {
				userRights = userRights | rs.getInt(1);
			}
		}
		return userRights;
	}

	public static int getUserWorkingRecordRights(
		User user,
		String recID,
		PageState state)
		throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs =
				conn.executeQuery(
					"SELECT User_Rights FROM Folder_Content_Short_View FC, Sample S, Record R "
						+ "WHERE FC.Feature_ID = S.Feature_ID AND R.Sample_ID = S.Sample_ID AND Record_ID = "
						+ recID
						+ " AND User_ID = "
						+ userID);
			while (rs.next()) {
				userRights = userRights | rs.getInt(1);
			}
		}
		return userRights;
	}

	public static int getUserFolderRights(
		User user,
		String folderID,
		PageState state)
		throws IOException, SQLException {
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs =
				conn.executeQuery(
					"SELECT User_Rights FROM Folder_View WHERE Folder_ID = "
						+ folderID
						+ " AND User_ID = "
						+ userID);
			if (rs.next()) {
				return rs.getInt(1);
			} else { //no record
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static int getSecurityType(
		int secClassID,
		User user,
		PageState state)
		throws IOException, SQLException {
		if (secClassID == 4)
			return 21; //public
		int secType;
		DBConnection conn = getIPConnection(state);
		ResultSet rs =
			conn.executeQuery(
				"SELECT COUNT(*) FROM user_right WHERE ur_sc_id = "
					+ secClassID);
		rs.next();
		if (rs.getInt(1) == 0) {
			rs =
				conn.executeQuery(
					"SELECT COUNT(*) FROM org_right WHERE or_sc_id = "
						+ secClassID);
			rs.next();
			if (rs.getInt(1) == 1) {
				rs =
					conn.executeQuery(
						"SELECT or_org_id FROM org_right WHERE or_sc_id = "
							+ secClassID);
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
			rs =
				conn.executeQuery(
					"SELECT ur_person_id FROM user_right WHERE ur_sc_id = "
						+ secClassID);
			rs.next();
			if (rs.getInt(1) == user.getPersonId()) {
				secType = 22;
			} else {
				throw new NoSuchSecurityClassException(secClassID);
			}
		} else {
			throw new NoSuchSecurityClassException(secClassID);
		}
		rs =
			conn.executeQuery(
				"SELECT COUNT(*) FROM group_right WHERE gr_id = 1 AND gr_sc_id = "
					+ secClassID);
		rs.next();
		if (rs.getInt(1) == 1)
			secType += 1;
		return secType;
	}

	public static int getSecurityClass(int secType, User user, PageState state)
		throws IOException, SQLException {
		int secClass = 0;
		DBConnection conn = getIPConnection(state);
		Statement statement = conn.getExtraStatement();
		ResultSet rs, rs2;
		switch (secType) {
			case 21 : //public
				secClass = 4;
				break;
			case 22 : //user
				rs =
					conn.executeQuery(
						"SELECT ur_sc_id FROM user_right, org_right, group_right WHERE ur_sc_id = or_sc_id(+) AND ur_sc_id = gr_sc_id(+) AND or_id IS NULL AND gr_id IS NULL AND ur_person_id = "
							+ user.getPersonId());
				while (rs.next()) {
					;
					rs2 =
						statement.executeQuery(
							"SELECT COUNT(*) FROM user_right WHERE ur_sc_id = "
								+ rs.getString(1)
								+ " AND ur_person_id <> "
								+ user.getPersonId());
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
				rs =
					conn.executeQuery(
						"SELECT ur_sc_id FROM user_right, org_right, group_right WHERE ur_sc_id = or_sc_id(+) AND ur_sc_id = gr_sc_id AND or_id IS NULL AND gr_id = 1 AND ur_person_id = "
							+ user.getPersonId());
				while (rs.next()) {
					;
					rs2 =
						statement.executeQuery(
							"SELECT COUNT(*) FROM user_right WHERE ur_sc_id = "
								+ rs.getString(1)
								+ " AND ur_person_id <> "
								+ user.getPersonId());
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
				rs =
					conn.executeQuery(
						"SELECT or_sc_id FROM org_right, user_right, group_right WHERE or_sc_id = ur_sc_id(+) AND or_sc_id = gr_sc_id(+) AND ur_id IS NULL AND gr_id IS NULL AND or_org_id = "
							+ user.getOrgId());
				while (rs.next()) {
					;
					rs2 =
						statement.executeQuery(
							"SELECT COUNT(*) FROM org_right WHERE or_sc_id = "
								+ rs.getString(1)
								+ " AND or_org_id <> "
								+ user.getOrgId());
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
				rs =
					conn.executeQuery(
						"SELECT or_sc_id FROM org_right, user_right, group_right WHERE or_sc_id = ur_sc_id(+) AND or_sc_id = gr_sc_id AND ur_id IS NULL AND gr_id = 1 AND or_org_id = "
							+ user.getOrgId());
				while (rs.next()) {
					;
					rs2 =
						statement.executeQuery(
							"SELECT COUNT(*) FROM org_right WHERE or_sc_id = "
								+ rs.getString(1)
								+ " AND or_org_id <> "
								+ user.getOrgId());
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

	private static int addUserRight(User user, PageState state, boolean paleo)
		throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc =
			AuthUtils.addSecurityClass(
				"FR",
				user,
				conn,
				"FRED Private User Class");
		sc.addUserToClass(user, new Right(1), conn);
		if (paleo)
			sc.addGroupToClass(1, new Right(1), conn);
		return sc.getId();
	}

	private static int addOrgRight(User user, PageState state, boolean paleo)
		throws IOException, SQLException {
		DBConnection conn = getIPConnection(state);
		SecurityClass sc =
			AuthUtils.addSecurityClass(
				"FR",
				user,
				conn,
				"FRED Private Org Class");
		sc.addUsersOrgToClass(user, new Right(1), conn);
		if (paleo)
			sc.addGroupToClass(1, new Right(1), conn);
		return sc.getId();
	}

	public static String formatDateForOutput(Date date, String rounding) {
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM yyyy");

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

	public static String formatDateForOutput(Date date) {
		return formatDateForOutput(date, null);
	}

	public static Sample getSampleAbove(
		Sample sample,
		User user,
		PageState state)
		throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = { Types.NUMERIC, Types.NUMERIC };
		Object data[] = new Object[2];
		String query =
			"SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth < ? ORDER BY Top_Depth DESC";
		data[0] = new Integer(sample.getAsInt(Sample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(Sample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return new Sample(rs.getInt(1), user, state);
	}

	public static Sample getSampleBelow(
		Sample sample,
		User user,
		PageState state)
		throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = { Types.NUMERIC, Types.NUMERIC };
		Object data[] = new Object[2];
		String query =
			"SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth > ? ORDER BY Top_Depth";
		data[0] = new Integer(sample.getAsInt(Sample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(Sample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return new Sample(rs.getInt(1), user, state);
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
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
