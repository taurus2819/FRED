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

import nz.cri.gns.auth.User;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FREDUtils implements FREDConstants {

	public static DBConnection getRecordConn(int recID, User user, PageState state) throws SQLException, IOException {
		DBConnection conn = JspUtils.createDatabaseConnection(state.getSession(), CONNECTION, DB_NAME, state.getContext());
		ResultSet rs = conn.executeQuery("SELECT Status FROM Record_All_View WHERE Record_ID = " + recID);
		if (rs.next()) {
			if (rs.getString(1).equals("approved")) {
				if (user !=  null) {
					try {
						return user.getUsersConnection(state, conn);
					} catch (Exception e) {
						return null;
					}
				} else {
					return conn;
				}
			} else {
				return conn;
			}
		} else {
			return null;			
		}
	}

}
