package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class TaxaPanelList {

	private Vector panels;

	public TaxaPanelList(User user, PageState state) throws IOException, SQLException {
		if (user != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			String query = ("SELECT group_id, group_name FROM taxa_panel_view WHERE panelist_id = ? ORDER BY group_name");
			ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(user.getPersonId())});
			panels = new Vector();
			while (rs.next()) {
				panels.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			conn.releaseStatement();
		}
	}
	
	public Vector getPanels() {
		return panels;
	}
	
	public int getPanelCount() {
		return panels.size();
	}

}
