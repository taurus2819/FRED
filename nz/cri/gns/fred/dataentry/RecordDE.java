package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public abstract class RecordDE implements DataEntryForm {

	public static final int WORKING_COMMENTS = 0;

	protected Sample sample;
	protected User user;
	protected PageState state;
	protected Folder folder;
	protected Record record;
	protected String recordType;
	protected String[] fields = new String[50];
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

	public RecordDE(int recID, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		this.user = user;
		this.state = state;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Sample_ID FROM Record WHERE Record_ID = " + recID);
		rs.next();
		this.sample = new Sample(rs.getInt(1), user, state);
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
	}

	protected void parseField(int field, String value) throws DataInputException {
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

	/* (non-Javadoc)
	 * @see nz.cri.gns.fred.dataentry.DataEntryForm#save()
	 */
	public int save() throws SQLException, IOException, InvalidCredentialsException {
		// TODO Auto-generated method stub
		return 0;
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
