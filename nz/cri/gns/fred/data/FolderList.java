package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class FolderList {

	private Vector personalFolders;
	private Vector adminFolders;

	public FolderList(User user, PageState state) throws IOException, SQLException {
		if (user != null) {
			int userID = user.getPersonId();
			DBConnection conn = FREDUtils.getFREDConnection(state);
			int[] types = { Types.NUMERIC };
			Object[] data = new Object[1];
			data[0] = new Integer(userID);
			String query = ("SELECT Folder_Type, Folder_ID FROM Folder_View WHERE User_ID = ? ORDER BY Folder_Name");
			ResultSet rs = conn.executeQuery(query, types, data);
			conn.preservePreparedStatement();
			personalFolders = new Vector();
			adminFolders = new Vector();
			while (rs.next()) {
				if (rs.getString(1).equals("personal")) {
					personalFolders.add(new Integer(rs.getInt(2)));
				} else {
					adminFolders.add(new Integer(rs.getInt(2)));
				}
			}
			conn.releaseStatement();
		}
	}
	
	public Vector getPersonalFolders() {
		return personalFolders;
	}
	
	public int getPersonalFolderCount() {
		return personalFolders.size();
	}

	public Vector getAdminFolders() {
		return adminFolders;
	}
	
	public int getAdminFolderCount() {
		return adminFolders.size();
	}

}
