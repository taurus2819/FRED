/*
 * Created on 12/01/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.User;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FREDUtils implements FREDConstants {

	public static DBConnection getFREDConnection(PageState state) throws IOException {
		return ExternalUtils.createDatabaseConnection(
			state.getSession(),
			FREDConstants.CONNECTION,
			FREDConstants.DB_NAME,
			state.getContext());
	}

	public static boolean isAllowedRecord(
		User user,
		int securityClassID,
		PageState state)
		throws IOException, SQLException {
		if (user == null)
			return false;
		DBConnection conn =
			ExternalUtils.createDatabaseConnection(
				state.getSession(),
				"nz.cri.gns.ip.connection",
				"ip",
				state.getContext());
		SecurityClass sc = new SecurityClass(securityClassID, conn);
		SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
		return sca.isAccessibleTo(user, conn);
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

	public static FullSample getSampleAbove(FullSample sample, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = {Types.NUMERIC, Types.NUMERIC};
		Object data[] = new Object[2];
		String query = "SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth < ? ORDER BY Top_Depth DESC";
		data[0] = new Integer(sample.getAsInt(FullSample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(FullSample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return FullSample.getFullSample(rs.getInt(1), user, state);
	}

	public static FullSample getSampleBelow(FullSample sample, User user, PageState state)
		throws SQLException, IOException, AccessDeniedException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		int[] types = {Types.NUMERIC, Types.NUMERIC};
		Object data[] = new Object[2];
		String query = "SELECT Sample_ID FROM FR.Sample_All_View WHERE Feature_ID = ? AND Top_Depth > ? ORDER BY Top_Depth";
		data[0] = new Integer(sample.getAsInt(FullSample.FEATURE_ID));
		data[1] = new Double(sample.getAsDouble(FullSample.TOP_DEPTH));
		ResultSet rs = conn.executeQuery(query, types, data);
		rs.next();
		return FullSample.getFullSample(rs.getInt(1), user, state);
	}

}
