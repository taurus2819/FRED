package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.AdoptionRecord;
import nz.cri.gns.fred.data.Audit;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public abstract class RecordDE implements DataEntryForm {

	protected Sample sample;
	protected User user;
	protected PageState state;
	protected Folder folder;
	protected Record record;
	protected String recordType;
	private Integer secClassID;
	protected String[] fields = new String[120];
	protected String[] tempFields = new String[120];
	protected boolean savedFlag = false;

	protected RecordDE(User user, int folderID, String recordType, PageState state) throws DataInputException, SQLException, IOException {
		this.user = user;
		this.state = state;
		if (!(recordType.equals(Record.ADOPTION_RECORD) || recordType.equals(Record.PALEONTOLOGY_RECORD)))
			throw new DataInputException("Record Type", "Invalid value");
		this.recordType = recordType;
		this.folder = new Folder(folderID, user, state);		
	}

	public RecordDE(User user, int sampleID, int folderID, String recordType, PageState state)
		throws SQLException, IOException, DataInputException {
			this(user, folderID, recordType, state);
			this.sample = new Sample(sampleID, user, state);
	}

	public RecordDE(int recID, String recordType, User user, PageState state) throws InvalidCredentialsException, DataInputException, SQLException, IOException {
		try {
			this.user = user;
			this.state = state;
			this.recordType = recordType;
			if (recordType.equals(Record.ADOPTION_RECORD)) {
				this.record = (AdoptionRecord) AdoptionRecord.getData(recID, user, state, true);
			} else if (recordType.equals(Record.PALEONTOLOGY_RECORD)) {
				this.record = (PaleontologyRecord) PaleontologyRecord.getData(recID, user, state, true);
			} else {
				throw new DataInputException("Record Type", "Invalid Value");
			}
			this.sample = new Sample(record.getAsInt(Record.SAMPLE_ID), user, state);
			setField(WORKING_COMMENTS, record.getAsString(Record.WORKING_COMMENTS));
			try {
				setField(SECURITY_TYPE, String.valueOf(FREDUtils.getSecurityType(record.getAsInt(Record.SECURITY_CLASS_ID), user, state)));
			} catch (Exception e) {
				setField(SECURITY_TYPE, "21");
			}
			if (record.getAsString(Record.STATUS).equals(Audit.STATUS_APPROVED)) {
				if (FREDUtils.hasMasterfileRecordRights(user, String.valueOf(recID), state)) {
					folder = new Folder(sample.getAsInt(Sample.MASTERFILE_ID), user, state);
				} else {
					throw new DataInputException("Record", "Record not editable");
				}
			} else {
				folder = new Folder(record.getAsInt(Record.WORKING_FOLDER_ID), user, state);
			}
		} catch (TaxonomicListException e) {
			throw new DataInputException("Taxonomic List",  "Data Error");
		}
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException, TaxonomicListException {
		if (value != null && (value.equals("") || value.equals("-") || value.equals("null")))
			value = null;
		if (value != null) {
			parseField(field, value);
		} else {
			resetHiddenField(field);
		}
		fields[field] = value;
		savedFlag = false;
	}

	public String getField(int field) {
		return fields[field];
	}

	public void setTempField(int field, String value) {
		tempFields[field] = value;
	}

	public String getTempField(int field) {
		return tempFields[field];
	}

	protected String getFieldForHTML(int field) {
		if (getTempField(field) != null) {
			return getTempField(field);
		}
		return getField(field);
	}

	public void setFieldsFromTemp() throws DataInputException, TaxonomicListException {
		for (int i = 0; i < getFieldCount(); i++) {
			setField(i, tempFields[i]);
			setTempField(i, null);
		}
	}

	public void setSample(Sample sample) {
		this.sample = sample;
		savedFlag = false;
	}

	protected void parseField(int field, String value) throws DataInputException, TaxonomicListException {
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

	protected void resetHiddenField(int field) {
	}

	public void makeNavPanelHTML(Writer out) throws IOException {
		out.write("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		if (recordType.equals(Record.ADOPTION_RECORD)) {
			out.write("<tr><td colspan='2' align='center'><img src='images/ado.gif' height='20' width='20' /></td></tr>");
			out.write("<tr><td colspan='2' align='center' class='heading'>Adoption Record</td></tr>\n");
		} else if (recordType.equals(Record.PALEONTOLOGY_RECORD)) {
			out.write("<tr><td colspan='2' align='center'><img src='images/pal.gif' height='20' width='20' /></td></tr>");
			out.write("<tr><td colspan='2' align='center' class='heading'>Paleontology Record</td></tr>\n");
		}
		out.write("<tr><td>&nbsp;</td></tr>");
		//out.write("<tr><td><a href='load_record.jsp?FoldID=" + folder.getFolderID());
		//if (record != null) out.write("&RecID=" + record.getRecordID());
		//out.write("&SampID=" + sample.getSampleID() + "&RecType=SMP'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID=" + folder.getFolderID());
		//if (record != null) out.write("&RecID=" + record.getRecordID());
		//out.write("&SampID=" + sample.getSampleID() + "&RecType=SMP' class='heading'>Copy From</a></td></tr>");
		out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (folder.isAllowedSubmitLocalities())
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		out.write("<tr><td><a href='javascript:history.back();'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a>&nbsp;&nbsp;</td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");
		out.write("</table>");
	}

	public void makeDataEntryHTML(Writer out) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		out.write("<table border='0' cellspacing='0' cellpadding='2'>\n");
		try {
			out.write("<tr><td class='heading'>Sample Name</td><td></td><td class='heading'>" + sample.getAsString(Sample.SAMPLE_NAME));
			out.write("<br>" + FREDUtils.noNulls(sample.getAsString(Sample.FEATURE_NAME)) + ": " + FREDUtils.noNulls(sample.getAsString(Sample.DRILLHOLE_DEPTH)));
		} catch (Exception e) {	}
		out.write("</td>");
		try {
			out.write("<td><a href='new_sample.jsp?FeatID=" + sample.getAsString(Sample.FEATURE_ID) + "&SampID=" + sample.getSampleID() + "&FoldID=" + sample.getAsString(Sample.FEATURE_WORKING_FOLDER_ID)
					 + ((record != null) ? "&RecID=" + record.getRecordID() : "")
					 + "&RecType=" + recordType + "'><img src='images/edit.gif' width='20' height='20' border='0' alt='Edit' /></a></td>");
		} catch (Exception e) {}
		out.write("</tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'>" + FREDUtils.noNulls(getFieldForHTML(WORKING_COMMENTS)) + "</textarea></td></tr>\n");
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
		
		out.write("<tr><td class='heading' colspan='2'>Security Setting</td><td>");
		ComboDescriptor cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
		cd.name = "SecType";
		if (getField(SECURITY_TYPE) != null) {
			cd.selected = getFieldForHTML(SECURITY_TYPE);
		} else {
			cd.selected = "21";
		}
		cd.orderBy = "Lookup_ID";
		cd.join = "FieldName = 'SecurityClass'";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
	}

	protected void makeEndBitHTML(Writer out) throws IOException {
		out.write("<table border='0' cellpadding='0' cellspacing='2'>\n");
		out.write("<tr><td>&nbsp;</td></tr>\n");
		out.write(
			"<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (folder.isAllowedSubmitLocalities()) {
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		}
		out.write("</table>\n");
	}

	public int save() throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			if (record == null) {
				if (!folder.isAllowedCreateLocalities()) throw new InvalidCredentialsException();
				//create new AUDIT record
				QueryDescriptor qd = new QueryDescriptor("audit_table");
				qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
				qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
				qd.addQueryColumn("working_comments", Types.VARCHAR, fields[WORKING_COMMENTS]);
				qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(folder.getFolderID()));
				qd.addQueryColumn("security_class_id", Types.NUMERIC, ((secClassID != null) ? secClassID : new Integer(4)));
				String auditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
				//create new RECORD record
				qd = new QueryDescriptor("record");
				qd.addQueryColumn("sample_id", Types.NUMERIC, new Integer(sample.getSampleID()));
				qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(auditID));
				String recordID = DBUtils.doInsertUsingSequence(qd, "record_id", "record_seq", conn, true);
				record = Record.getData(Integer.parseInt(recordID), user, state, true);
			} else { // edit
				if ((!FREDUtils.hasMasterfileRecordRights(user, String.valueOf(record.getRecordID()), state) && record.getAsString(Record.STATUS).equals(Audit.STATUS_APPROVED)) || !folder.isAllowedEditLocalities())
					throw new InvalidCredentialsException();
				//Update AUDIT
				QueryDescriptor qd = new QueryDescriptor("audit_table");
				qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
				qd.addQueryColumn("modified_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
				qd.addQueryColumn("modified_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
				qd.addQueryColumn("working_comments", Types.VARCHAR, fields[WORKING_COMMENTS]);
				qd.addQueryColumn("security_class_id", Types.NUMERIC, ((secClassID != null) ? secClassID : new Integer(4)));
				qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(record.getAsInt(Record.AUDIT_ID)));
				DBUtils.doUpdate(qd, "audit_id = ?", conn);
				record = Record.getData(record.getRecordID(), user, state, true);
			}
			conn.releaseStatement();
		}
		savedFlag = true;
		return record.getRecordID();
	}


	public int submit()
		throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InvalidCredentialsException();
		checkMandatoryFields();
		int recordID = save();
		//change status
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_APPROVED);
		qd.addQueryColumn("submitted_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
		qd.addQueryColumn("submitted_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
		qd.addQueryColumn("working_comments", Types.VARCHAR, null);
		qd.addQueryColumn("working_folder_id", Types.NUMERIC, null);
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(record.getAsInt(Record.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		conn.releaseStatement();
		record = Record.getData(record.getRecordID(), user, state, true);
		return recordID;
	}
	
	protected void checkMandatoryFields() throws DataInputException {
	}
	
	public void delete() throws IOException, SQLException, InvalidCredentialsException {
		if (record != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.executeUpdate("DELETE FROM record WHERE record_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(record.getRecordID())});
			conn.releaseStatement();
		}
	}
	
	public int getWorkingFolderID() {
		return folder.getFolderID();
	}

}
