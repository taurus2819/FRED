package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents an Audit record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getAudit method instead.
 */
public class FolderList implements FREDConstants {

	private Vector personalFolders;
	private Vector adminFolders;

	public FolderList(User user, PageState state) throws IOException, SQLException {
		if (user != null) {
			int userID = user.getPersonId();
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs = conn.executeQuery("SELECT Folder_Type, Folder_ID FROM Folder_View WHERE User_ID = " + userID);
			personalFolders = new Vector();
			adminFolders = new Vector();
			while (rs.next()) {
				if (rs.getString(1).equals("personal")) {
					personalFolders.add(new Folder(rs.getInt(2), user, state));
				} else {
					adminFolders.add(new Folder(rs.getInt(2), user, state));
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
