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
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Record implements FREDConstants {

	private int recordID;
    private String recordType;
	private int featureID;
	private int sampleID;
    
	protected Record(int id, PageState state) throws SQLException, IOException {
		this.recordID = id;
		loadRecordData(state); 
	}
	
	protected void loadRecordData(PageState state) throws SQLException, IOException {
		DBConnection conn = JspUtils.createDatabaseConnection(state.getSession(), CONNECTION, DB_NAME, state.getContext());
		ResultSet rs = conn.executeQuery("SELECT Record_Type, Feature_ID, Sample_ID FROM Record_View WHERE Record_ID = " + this.getRecordID());
		if (rs.next()) {
			setRecordType(rs.getString(1));
			setFeatureID(rs.getInt(2));
			setSampleID(rs.getInt(3));
		}
	}

	public int getRecordID() {
		return recordID;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}

	public int getFeatureID() {
		return featureID;
	}

	public void setSampleID(int sampleID) {
		this.sampleID = sampleID;
	}

	public int getSampleID() {
		return sampleID;
	}
}

