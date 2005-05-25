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
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = ("SELECT folder_type, folder_id, folder_name, user_rights, folder_owner FROM folder_view WHERE user_id = ? ORDER BY folder_name");
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(user.getPersonId())});
			personalFolders = new Vector();
			adminFolders = new Vector();
			while (rs.next()) {
				if (rs.getString(1).equals("personal")) {
					personalFolders.add(new FolderSkeleton(rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)));
				} else {
					adminFolders.add(new FolderSkeleton(rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)));
				}
			}
			rs.close();
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
