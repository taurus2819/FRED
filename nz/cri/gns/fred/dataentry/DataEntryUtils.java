package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;


public class DataEntryUtils {

	public static void parseDropDownID(String fieldName, String SQL, PageState state) throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs = conn.executeQuery(SQL);
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

	public static RoundedDate parseRoundedDate(String dateStr)
		throws DataInputException {
		String date, dateRnd, day, month, year;
		if (dateStr.lastIndexOf("/") == dateStr.length() - 1)
			throw new DataInputException("Date", "Invalid Data");
		//ends with slash
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
				year =
					dateStr.substring(
						dateStr.indexOf("/") + 1,
						dateStr.length());
			} else {
				dateRnd = null;
				day = dateStr.substring(0, dateStr.indexOf("/"));
				month =
					dateStr.substring(
						dateStr.indexOf("/") + 1,
						dateStr.lastIndexOf("/"));
				year =
					dateStr.substring(
						dateStr.lastIndexOf("/") + 1,
						dateStr.length());
			}
			try {
				int iDay = Integer.parseInt(day);
				int iMonth = Integer.parseInt(month);
				Integer.parseInt(year);
				if (iDay < 0 || iDay > 31)
					throw new DataInputException("Date", "Invalid Data");
				//bad day
				if (iMonth < 0 || iMonth > 12)
					throw new DataInputException("Date", "Invalid Data");
				//bad month
				if (year.length() != 4)
					throw new DataInputException("Date", "Invalid Data");
				//bad year
				if (iMonth == 2 && iDay > 28)
					throw new DataInputException("Date", "Invalid Data");
				//bad Feb
				if ((iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11)
					&& iDay > 30)
					throw new DataInputException("Date", "Invalid Data");
				//bad 30 day months
			} catch (Exception e) {
				throw new DataInputException("Date", "Invalid Data");
			}
			date = day + "/" + month + "/" + year;
			return new RoundedDate(date, dateRnd);
		}
	}

	public static String getStageID(String stageStartID, String startMod, String stageStopID, String stopMod, PageState state) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String stageID = null;
		if (stageStartID != null) {
			ResultSet rs = conn.executeQuery("SELECT Get_Stage_ID(" + stageStartID + ", " + JspUtils.sqlEscape(startMod) + ", " + JspUtils.sqlEscape(stageStopID) + ", " + JspUtils.sqlEscape(stopMod) + ") FROM DUAL");
			rs.next();
			if (rs.getString(1) != null) {
				stageID = rs.getString(1);
			} else {
				rs = conn.executeQuery("SELECT Stage_Seq.NEXTVAL FROM DUAL");
				rs.next();
				stageID = rs.getString(1);
				conn.executeUpdate("INSERT INTO Stage (Stage_ID, Stage_Lower_ID, Stage_Lower_Mod, Stage_Upper_ID, Stage_Upper_Mod) VALUES (" + stageID + ", " + stageStartID + ", " + JspUtils.sqlEscape(startMod) + ", " + JspUtils.sqlEscape(stageStopID) + ", " + JspUtils.sqlEscape(stopMod) + ")");
			}
		}
		return stageID;
	}
}
