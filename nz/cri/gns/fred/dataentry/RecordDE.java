package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.AccessDeniedException;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public abstract class RecordDE implements DataEntryForm {

	protected Sample sample;
	protected User user;
	protected PageState state;
	protected Folder folder;
	protected Record record;
	protected String recordType;
	private Integer secClassID;
	protected String[] fields = new String[100];
	protected boolean savedFlag = false;

	public RecordDE(User user, int folderID, String recordType, PageState state) throws DataInputException, SQLException, IOException {
		this.user = user;
		this.state = state;
		if (!(recordType.equals("SMP") || recordType.equals("ADO") || recordType.equals("PAL")))
			throw new DataInputException("Record Type", "Invalid value");
		this.recordType = recordType;
		this.folder = new Folder(folderID, user, state);		
	}

	public RecordDE(User user, int sampleID, int folderID, String recordType, PageState state)
		throws SQLException, IOException, DataInputException {
			this(user, folderID, recordType, state);
			this.sample = new Sample(sampleID, user, state);
	}

	public RecordDE(int recID, User user, PageState state) throws InvalidCredentialsException, DataInputException, SQLException, IOException, AccessDeniedException {
		this.user = user;
		this.state = state;
		this.record = Record.getData(recID, user, state);
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Sample_ID FROM Record WHERE Record_ID = " + recID);
		rs.next();
		this.sample = new Sample(rs.getInt(1), user, state);
		setField(WORKING_COMMENTS, record.getAsString(Record.WORKING_COMMENTS));
		try {
			setField(SECURITY_TYPE, String.valueOf(FREDUtils.getSecurityType(record.getAsInt(Record.SECURITY_CLASS_ID), user, state)));
		} catch (Exception e) {
			setField(SECURITY_TYPE, "21");
		}
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException {
		if (value != null && (value.equals("") || value.equals("-") || value.equals("null")))
			value = null;
		if (value != null)
			parseField(field, value);
		fields[field] = value;
		savedFlag = false;
	}

	public String getField(int field) {
		return fields[field];
	}

	public void setSample(Sample sample) {
		this.sample = sample;
		savedFlag = false;
	}

	protected void parseField(int field, String value) throws DataInputException {
		switch (field) {
			case SECURITY_TYPE :
				try {
					secClassID = new Integer(FREDUtils.getSecurityClass(Integer.parseInt(value), user, state));
				} catch (Exception e) {
					throw new DataInputException("Security Class", "Invalid");
				}
				break;
		}
	}

	protected void parseAge(String stageStart, String stageStop, String fieldName) throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs = conn.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStart);
			rs.next();
			double startStart = rs.getDouble(1);
			double startStop = rs.getDouble(2);
			if (stageStop != null) {
				rs = conn.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStop);
				rs.next();
				double stopStart = rs.getDouble(1);
				double stopStop = rs.getDouble(2);
				if (startStart < stopStart || startStop < stopStop) throw new DataInputException(fieldName, "Stop age younger than start age");
			}
		} catch (Exception e) {
			throw new DataInputException(fieldName, "Invalid");
		}
	}

	public void makeNavPanelHTML(Writer out) throws IOException {
		out.write("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.write("<tr><td colspan='2' align='center'><img src='images/sprop.gif' height='20' width='20' /></td></tr>");
		out.write("<tr><td colspan='2' align='center' class='heading'>\n");
		if (recordType.equals("SMP")) {
			out.write("Sample Property");
		} else if (recordType.equals("ADO")) {
			out.write("Adoption");
		} else if (recordType.equals("PAL")) {
			out.write("Paleontology");
		}
		out.write(" Record</td></tr>\n");
		out.write("<tr><td>&nbsp;</td></tr>");
		out.write("<tr><td><a href='load_record.jsp?FoldID=" + folder.getFolderID());
		if (record != null) out.write("&RecID=" + record.getRecordID());
		out.write("&SampID=" + sample.getSampleID() + "&RecType=SMP' title='Copy From'><img src='images/load.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='load_record.jsp?FoldID=" + folder.getFolderID());
		if (record != null) out.write("&RecID=" + record.getRecordID());
		out.write("&SampID=" + sample.getSampleID() + "&RecType=SMP' class='heading'>Copy From</a></td></tr>");
		out.write("<tr><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' title='Save'><img src='images/save.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' class='heading'>Save</a></td></tr>");
		if (folder.isAllowedSubmitLocalities()) {
			out.write("<tr><td><a href='#' onClick='if (submitForm(form1)) {form1.submit();}' title='Submit to Database'><img src='images/submit.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' class='heading' onClick='if (submitForm(form1)) {form1.submit();}' class='heading'>Submit</a></td></tr>");
		}
		out.write("<tr><td><a href='folder_detail.jsp?ID=" + folder.getFolderID() + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + folder.getFolderID() + "' class='heading'>Quit</a></td></tr>");
		out.write("</table>");
	}

	public void makeDataEntryHTML(Writer out) throws SQLException, IOException {
		out.write("<table border='0' cellspacing='0' cellpadding='2'>\n");
		try {
			out.write("<tr><td class='heading'>Sample Name</td><td></td><td class='heading'>" + sample.getAsString(Sample.SAMPLE_NAME));
			if (sample.getAsString(Sample.FEATURE_TYPE).equals("Outcrop")) out.write("<br>" +FREDUtils.noNulls(sample.getAsString(Sample.FEATURE_NAME)) + ": " + FREDUtils.noNulls(sample.getAsString(Sample.DRILLHOLE_DEPTH)));
		} catch (Exception e) {	}
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'>" + FREDUtils.noNulls(getField(WORKING_COMMENTS)) + "</textarea></td></tr>\n");
/*			if (!recID.equals("0")) {
				out.println("<tr><td class='heading' colspan='2'>Attached Files/Images<br><span class='smalltext'>Click <a href='binary_data_entry.jsp?RecID=" + recID + "&RecType=SMP&FoldID=" + foldID + "'>here</a> to add/edit</span></td><td>");
				MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
				if (mr != null) {
					for (int i = 0; i < mr.length; i++) {
						out.println(mr[i].getTitle() + "<br>");
					}
				}
				out.println("</td></tr>");
			}
*/
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
	}

	protected void makeEndBitHTML(Writer out) throws IOException {
		out.write("<table border='0' cellpadding='0' cellspacing='2'>\n");
		out.write("<tr><td>&nbsp;</td></tr>\n");
		out.write("<tr><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' title='Save'><img src='images/save.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' class='heading'>Save</a></td></tr>\n");
		if (folder.isAllowedSubmitLocalities())
			out.write("<tr><td><a href='#' onClick='if (submitForm(form1)) {form1.submit();}' title='Submit to Database'><img src='images/submit.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' class='heading' onClick='if (submitForm(form1)) {form1.submit();}' class='heading'>Submit</a></td></tr>\n");
		out.write("</table>\n");
	}

	public int save() throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			if (record == null) {
				if (!folder.isAllowedCreateLocalities())
					throw new InvalidCredentialsException();
				//create new AUDIT and RECORD records
				rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				String auditID = rs.getString(1);
				conn.executeUpdate(
					"INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID, Security_Class_ID) VALUES ("
						+ auditID
						+ ", 'working', "
						+ user.getPersonId()
						+ ", SYSDATE, "
						+ JspUtils.sqlEscape(fields[WORKING_COMMENTS])
						+ ", "
						+ folder.getFolderID()
						+ ", " 
						+ secClassID.toString()
						+ ")");
				rs = conn.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
				rs.next();
				int recordID = rs.getInt(1);
				conn.executeUpdate(
					"INSERT INTO Record (Record_ID, Sample_ID, Audit_ID) VALUES ("
						+ recordID
						+ ", "
						+ sample.getSampleID()
						+ ", "
						+ auditID
						+ ")");
				try {
					record = Record.getData(recordID, user, state, true);
				} catch (Exception e) {}
			} else { // edit
				if (!folder.isAllowedEditLocalities())
					throw new InvalidCredentialsException();
				//Update AUDIT
				conn.executeUpdate(
					"UPDATE Audit_Table SET Modified_By_ID = "
						+ user.getPersonId()
						+ ", Modified_Date = SYSDATE, Working_Comments = "
						+ JspUtils.sqlEscape(fields[WORKING_COMMENTS])
						+ ", Security_Class_ID = "
						+ secClassID.toString()
						+ " WHERE Audit_ID = "
						+ record.getAsString(Record.AUDIT_ID));
				conn.executeUpdate(
					"UPDATE Record SET Sample_ID = "
						+ sample.getSampleID()
						+ " WHERE Record_ID = "
						+ record.getRecordID());
				try {
					record = Record.getData(record.getRecordID(), user, state, true);
				} catch (Exception e) {}
			}
			conn.releaseStatement();
		}
		savedFlag = true;
		return record.getRecordID();
	}

	/* (non-Javadoc)
	 * @see nz.cri.gns.fred.dataentry.DataEntryForm#submit()
	 */
	public int submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see nz.cri.gns.fred.dataentry.DataEntryForm#delete()
	 */
	public void delete() throws IOException, SQLException, InvalidCredentialsException {
		// TODO Auto-generated method stub
		
	}

}
