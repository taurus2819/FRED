package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * @author iainm
 */
public class FolderSkeleton extends KeyValueObject implements FREDFolder {

	private int userRights;
	private String owner;

	public FolderSkeleton(String id, String name, int rights, String owner) {
		super(id, name);
		this.owner = owner;
		this.userRights = rights;
	}
	
	public String getOwner() {
		return owner;
	}
	public int getUserRights() {
		return userRights;
	}
	
	public boolean isAllowedReadLocalities() {
		return ((userRights & FOLDER_READ_RIGHT) != 0);
	}
	
	public boolean isAllowedEditLocalities() {
		return ((userRights & FOLDER_EDIT_RIGHT) != 0);
	}

	public boolean isAllowedCreateLocalities() {
		return ((userRights & FOLDER_CREATE_RIGHT) != 0);
	}

	public boolean isAllowedDeleteLocalities() {
		return ((userRights & FOLDER_DELETE_RIGHT) != 0);
	}

	public boolean isAllowedSubmitLocalities() {
		return ((userRights & FOLDER_SUBMIT_RIGHT) != 0);
	}

	public boolean isAllowedAdmin() {
		return ((userRights & FOLDER_ADMIN_RIGHT) != 0);
	}

	public boolean isAllowedApproveLocalities() {
		return ((userRights & FOLDER_APPROVE_RIGHT) != 0);
	}

	public int getLocalityCount(PageState state) throws InvalidCredentialsException, IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT count(feature_id) FROM folder_content_view WHERE folder_id = ?", 
				new int[] {Types.INTEGER},
				new Object[] {new Integer(key)}
		);
		int count = rs.getInt(1);
		conn.releaseStatement();
		return count;
	}
}
