package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.AccessDeniedException;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class FREDUtils {

	public static final String CONNECTION = "nz.cri.gns.fr.connection";
	public static final String DB_NAME = "fr";

	public static DBConnection getFREDConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(
			state.getSession(),
			CONNECTION,
			DB_NAME,
			state.getContext());
	}

	public static boolean isAllowedLocality(User user, String securityClassID, String status, String featID, PageState state) throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals("approved")) {
			return ((getUserWorkingLocalityRights(user, featID, state) & 1) > 0);
		}
		if (securityClassID != null) {
			DBConnection conn =	JspUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.ip.connection",
					"ip",
					state.getContext());
			SecurityClass sc = new SecurityClass(Integer.parseInt(securityClassID), conn);
			SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
			return sca.isAccessibleTo(user, conn);
		} else {
			return false;
		}
	}

	public static boolean isAllowedRecord(User user, String securityClassID, String status, String recID, PageState state) throws IOException, SQLException {
		if (user == null)
			return false;
		if (!status.equals("approved")) {
			return ((getUserWorkingRecordRights(user, recID, state) & 1) > 0);
		}
		if (securityClassID != null) {
			DBConnection conn =
				JspUtils.createDatabaseConnection(
					state.getSession(),
					"nz.cri.gns.ip.connection",
					"ip",
					state.getContext());
			SecurityClass sc = new SecurityClass(Integer.parseInt(securityClassID), conn);
			SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
			return sca.isAccessibleTo(user, conn);
		} else {
			return false;
		}
	}

	public static int getUserWorkingLocalityRights(User user, String featID, PageState state) throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs = conn.executeQuery("SELECT DISTINCT User_Rights FROM Folder_Content_Short_View WHERE Feature_ID = " + featID + " AND User_ID = " + userID);
			while (rs.next()) {
				userRights = userRights | rs.getInt(1);
			}
		}
		return userRights;	
	}

	public static int getUserWorkingRecordRights(User user, String recID, PageState state) throws IOException, SQLException {
		int userRights = 0;
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs = conn.executeQuery("SELECT User_Rights FROM Folder_Content_Short_View NATURAL JOIN Record WHERE Record_ID = " + recID + " AND User_ID = " + userID);
			while (rs.next()) {
				userRights = userRights | rs.getInt(1);
			}
		}
		return userRights;		
	}

	public static int getUserFolderRights(User user, String folderID, PageState state) throws IOException, SQLException {
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int userID = user.getPersonId();
			ResultSet rs = conn.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + folderID + " AND User_ID = " + userID);
			if (rs.next()) {
				return rs.getInt(1);
			} else { //no record
				return 0;
			}
		} else {
			return 0;
		}
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

	public static Sample getSampleAbove(Sample sample, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = {Types.NUMERIC, Types.NUMERIC};
		Object data[] = new Object[2];
		String query = "SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth < ? ORDER BY Top_Depth DESC";
		data[0] = new Integer(sample.getAsInt(Sample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(Sample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return new Sample(rs.getInt(1), user, state);
	}

	public static Sample getSampleBelow(Sample sample, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = {Types.NUMERIC, Types.NUMERIC};
		Object data[] = new Object[2];
		String query = "SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth > ? ORDER BY Top_Depth";
		data[0] = new Integer(sample.getAsInt(Sample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(Sample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return new Sample(rs.getInt(1), user, state);
	}

	public static String reverseParseDate(Date date, String dateRnd) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/yyyy");
		SimpleDateFormat monthDateFormatter = new SimpleDateFormat("M/yyyy");
		SimpleDateFormat yearDateFormatter = new SimpleDateFormat("yyyy");
		if (date != null) {
			if (dateRnd == null) {
				return dateFormatter.format(date);
			} else if (dateRnd.equals("Month")) {
				return monthDateFormatter.format(date);
			} else if (dateRnd.equals("Year")) {
				return yearDateFormatter.format(date);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static RoundedDate parseRoundedDate(String dateStr) throws DataInputException {
		String date, dateRnd, day, month, year;
		if (dateStr.lastIndexOf("/") == dateStr.length() - 1) throw new DataInputException("Date", "Invalid Data"); //ends with slash
		if (dateStr.indexOf("/") == -1 && dateStr.length() == 4) { //year only
			try {
				date = "1/1/" + Integer.parseInt(dateStr);
				return new RoundedDate(date, "Year");
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Data");
			}
		} else {
			if (dateStr.indexOf("/") == dateStr.lastIndexOf("/")) {
				dateRnd = "Month";
				day = "1";
				month = dateStr.substring(0, dateStr.indexOf("/"));
				year = dateStr.substring(dateStr.indexOf("/") + 1, dateStr.length());
			} else {
				dateRnd = null;
				day = dateStr.substring(0, dateStr.indexOf("/"));
				month = dateStr.substring(dateStr.indexOf("/") + 1, dateStr.lastIndexOf("/"));
				year = dateStr.substring(dateStr.lastIndexOf("/") + 1, dateStr.length());
			}
			try {
				int iDay = Integer.parseInt(day);
				int iMonth = Integer.parseInt(month);
				int iYear = Integer.parseInt(year);
				if (iDay < 0 || iDay > 31) throw new DataInputException("Date", "Invalid Data"); //bad day
				if (iMonth < 0 || iMonth > 12) throw new DataInputException("Date", "Invalid Data"); //bad month
				if (year.length() != 4) throw new DataInputException("Date", "Invalid Data"); //bad year
				if (iMonth == 2 && iDay > 28) throw new DataInputException("Date", "Invalid Data"); //bad Feb
				if ((iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11) && iDay > 30) throw new DataInputException("Date", "Invalid Data"); //bad 30 day months
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Data");
			}
			date = day + "/" + month + "/" + year;
			return new RoundedDate(date, dateRnd);
		}
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

	public static String makeNulls(String in) {
		return (in == null || in.length() == 0) ? null : "'" + in + "'";
	}

	public static String makeDropDownNulls(String in) {
		return (in == null || in.equals("-")) ? null : "'" + in + "'";
	}
}
