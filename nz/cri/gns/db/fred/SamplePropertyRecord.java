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

import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SamplePropertyRecord extends Record {

    private String stratUnit; 
    
	public SamplePropertyRecord(int id, PageState state) throws SQLException, IOException {
		super(id, state);
		loadData(state);
	}

	private void loadData(PageState state) throws SQLException, IOException {
		DBConnection conn = JspUtils.createDatabaseConnection(state.getSession(), CONNECTION, DB_NAME, state.getContext());
		ResultSet rs = conn.executeQuery("SELECT Strat_Unit FROM Sample_Property_All_View WHERE Record_ID = " + this.getRecordID());
		if (rs.next()) {
			this.stratUnit = rs.getString(1);
		}
	}

	public String getStratUnit() {
		return stratUnit;
	}

}
