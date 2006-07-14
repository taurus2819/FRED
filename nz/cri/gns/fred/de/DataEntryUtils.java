package nz.cri.gns.fred.de;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;


public class DataEntryUtils {

	public static DBConnection getFREDConnection(PageState state) throws IOException {
		return JspUtils.createDatabaseConnection(state.session, "fred", "fr", state.context);
	}
	
	public static void checkDropDownID(String fieldName, String query, int[] types, Object[] values, PageState state) throws DataInputException {
		try {
			ResultSet rs = getFREDConnection(state).executeQuery(query, types, values);
			rs.next();
		} catch (Exception e) {
			throw new DataInputException(fieldName, "Invalid value");
		}
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
		if (dateStr.lastIndexOf("/") == dateStr.length() - 1)
			throw new DataInputException("Date", "Invalid Data");
		//ends with slash
		if (dateStr.indexOf("/") == -1 && dateStr.length() == 4) { //year only
			try {
				date = Integer.parseInt(dateStr) + "-01-01";
				return new RoundedDate(java.sql.Date.valueOf(date), "Year");
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
				month =	dateStr.substring(dateStr.indexOf("/") + 1,	dateStr.lastIndexOf("/"));
				year = dateStr.substring(dateStr.lastIndexOf("/") + 1, dateStr.length());
			}
			try {
				int iDay = Integer.parseInt(day);
				int iMonth = Integer.parseInt(month);
				Integer.parseInt(year);
				if (iDay < 0 || iDay > 31)
					throw new DataInputException("Date", "Invalid Data"); //bad day
				if (iMonth < 0 || iMonth > 12)
					throw new DataInputException("Date", "Invalid Data"); //bad month
				if (year.length() != 4)
					throw new DataInputException("Date", "Invalid Data"); //bad year
				if (iMonth == 2 && iDay > 28)
					throw new DataInputException("Date", "Invalid Data"); //bad Feb
				if ((iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11) && iDay > 30) //bad 30 day months
					throw new DataInputException("Date", "Invalid Data");
				date = year + "-" + ((iMonth < 10) ? "0" + String.valueOf(iMonth) : String.valueOf(iMonth)) + "-" + ((iDay < 10) ? "0" + String.valueOf(iDay) : String.valueOf(iDay));
				return new RoundedDate(java.sql.Date.valueOf(date), dateRnd);
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Data");
			}
		}
	}

	public static String getStageID(String stageStartID, String startMod, String stageStopID, String stopMod, PageState state) throws IOException, SQLException {
		if (stageStartID != null) {
			DBConnection conn = getFREDConnection(state);
			String query = "SELECT Get_Stage_ID(?, ?, ?, ?) FROM DUAL";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR},
				new Object[] {new Integer(stageStartID), startMod, ((stageStopID != null) ? new Integer(stageStopID) : null), stopMod});
			rs.next();
			if (rs.getString(1) != null)
				return rs.getString(1);
			QueryDescriptor qd = new QueryDescriptor("stage");
			qd.addQueryColumn("stage_lower_id", Types.NUMERIC, new Integer(stageStartID));
			qd.addQueryColumn("stage_lower_mod", Types.VARCHAR, startMod);
			if (stageStopID != null)
				qd.addQueryColumn("stage_upper_id", Types.NUMERIC, new Integer(stageStopID));
			qd.addQueryColumn("stage_upper_mod", Types.VARCHAR, stopMod);
			return DBUtils.doInsertUsingSequence(qd, "stage_id", "stage_seq", conn, true);
		}
		return null;
	}
	
	public static void parseAge(String stageStart, String stageStop, String fieldName, PageState state) throws DataInputException, IOException, NumberFormatException, SQLException {
		try {
			DBConnection conn = getFREDConnection(state);
			String query = "SELECT ta_age_start, ta_age_stop FROM age_view WHERE ag_id = ?";
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(stageStart)});
			rs.next();
			double startStart = rs.getDouble(1);
			double startStop = rs.getDouble(2);
			if (stageStop != null) {
				rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(stageStop)});
				rs.next();
				double stopStart = rs.getDouble(1);
				double stopStop = rs.getDouble(2);
				if (startStart < stopStart || startStop < stopStop)
					throw new DataInputException(fieldName, "Stop age younger than start age");
			}
		} catch (Exception e) {
			throw new DataInputException(fieldName, "Invalid");
		}
	}
	
}
