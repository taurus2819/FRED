package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class PaleontologyRecordDE extends RecordDE {

	private RoundedDate identDate;
	private Vector identifiers;
	private String lab;

	public PaleontologyRecordDE(User user, int sampleID, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(User user, int folderID, PageState state)
		throws DataInputException, SQLException, IOException {
		super(user, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(int recID, User user, PageState state)
		throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, "PAL", user, state);
		setField(IDENTIFICATION_DATE,
			DataEntryUtils.reverseParseDate(
				record.getAsDate(Record.IDENTIFICATION_DATE),
				record.getAsString(Record.IDENTIFICATION_DATE_ROUNDING)));
		if (record.get(Record.IDENTIFIER) != null) {
			StringBuffer identName = new StringBuffer();
			for (Iterator i = record.getAsVector(Record.IDENTIFIER).iterator(); i.hasNext();) {
				KeyValueObject ident = (KeyValueObject) i.next();
				identName.append(ident.getValue() + "\n");
			}
			setField(IDENTIFIERS, identName.toString());
		}
		setField(
			IDT_AGE_START,
			record.getAsString(Record.STAGE_LOWER_ID));
		setField(
			IDT_START_MOD,
			record.getAsString(Record.STAGE_LOWER_MOD));
		setField(
			IDT_AGE_STOP,
			record.getAsString(Record.STAGE_UPPER_ID));
		setField(
			IDT_STOP_MOD,
			record.getAsString(Record.STAGE_UPPER_MOD));
		setField(STAGE_COMMENTS, record.getAsString(Record.STAGE_COMMENTS));
		setField(LAB_SECTION, record.getAsString(Record.LAB_SECTION_ID));
		setField(LAB_NUMBER, record.getAsString(Record.LAB_NUMBER));
		setField(COLLECTION_COMMENTS, record.getAsString(Record.COLLECTION_COMMENTS));
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case IDENTIFICATION_DATE :
					identDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case IDENTIFIERS :
					identifiers = new Vector();
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";
						rs =
							conn.executeQuery(
								"SELECT Person_ID FROM Person_View WHERE Name = "
									+ JspUtils.sqlEscape(
										value
											.substring(0, value.indexOf("\n"))
											.trim()));
						try {
							rs.next();
							identifiers.add(new Integer(rs.getInt(1)));
						} catch (Exception e) {
							throw new DataInputException(
								"Identifier",
								value.substring(0, value.indexOf("\n")).trim()
									+ " not in database - add through builder");
						}
						value =
							value.substring(
								value.indexOf("\n") + 1,
								value.length());
					}
					break;
				case IDT_AGE_START :
					parseAge(value, getField(IDT_AGE_STOP), "Age");
					break;
				case IDT_AGE_STOP :
					parseAge(getField(IDT_AGE_START), value, "Age");
					break;
				case IDT_START_MOD :
				case IDT_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException(
							"Age",
							"Bad Modifier");
					break;
				case LAB_SECTION :
					rs = conn.executeQuery("SELECT Lab_ID FROM Lab_Section WHERE Lab_Section_ID = " + value);
					try {
						rs.next();
						lab = rs.getString(1);
					} catch (Exception e) {
						throw new DataInputException("Laboratory", "Value not in list");
					}
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException e) {
			throw new DataInputException();
		}
	}

	protected void resetHiddenField(int field) {
		switch (field) {
			case ADOPTION_DATE :
				identDate = null;
				break;
			case ADOPTORS :
				identifiers = null;
				break;
			case LAB_SECTION :
				lab = null;
				break;
		}
	}
	
	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			super.makeDataEntryHTML(out);
			out.write(
				"<tr><td class='heading'>Identification Date</td><td></td><td><input type='text' name='PalDate' value='"
					+ FREDUtils.noNulls(getField(IDENTIFICATION_DATE))
					+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=PalDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write(
				"<tr><td class='heading'>Identifiers</td><td></td><td><textarea name='Identifier' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getField(IDENTIFIERS))
					+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Identifier\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write(
				"<tr><td class='heading' colspan='2'>Stage Limits</td><td>\n");
			out.write("<table border='0' cellspacing='0'><tr><td>");
			ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStart";
			cd.prompt = "-- Choose --";
			cd.selected = getField(IDT_AGE_START);
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write(
				"</td><td><select name='StartMod'><option value='-' "
					+ ((getField(IDT_START_MOD) == null) ? " selected" : "")
					+ "></option><option value='?' "
					+ ((getField(IDT_START_MOD) != null
						&& getField(IDT_START_MOD).equals("?"))
						? " selected"
						: "")
					+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
			out.write("<tr><td>");
			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStop";
			cd.prompt = "-- Choose --";
			cd.selected = getField(IDT_AGE_STOP);
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write(
				"</td><td class='heading'><select name='StopMod'><option value='-' "
					+ ((getField(IDT_STOP_MOD) == null) ? " selected" : "")
					+ "></option><option value='?' "
					+ ((getField(IDT_STOP_MOD) != null
						&& getField(IDT_STOP_MOD).equals("?"))
						? " selected"
						: "")
					+ ">?</option></select></td></tr>\n");
			out.write("</table></td></tr>\n");
			out.write(
				"<tr><td class='heading' colspan='2'>Stage Comments</td><td><textarea name='StComm' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getField(STAGE_COMMENTS))
					+ "</textarea></td></tr>\n");
					
			//build array of labs sections
			out.write("<script language='JavaScript'>\n");
			ResultSet rs = conn.executeQuery("SELECT DISTINCT Lab_ID FROM Lab_Section");
			while (rs.next()) {
				out.write("a" + rs.getString(1) + " = new Array();\n");
				ResultSet rs2 = conn.getExtraStatement().executeQuery("SELECT Lab_Section_ID, Code FROM Lab_Section WHERE Lab_ID = " + rs.getString(1));
				int count = 0;
				while (rs2.next()) {
					out.write("a" + rs.getString(1) + "[" + count++ + "] = new Array('" + rs2.getString(1) + "','" + rs2.getString(2) + "');\n");
				}
			}
			out.write("function swapSection(frm){\n");
			out.write("if (frm.LabID.options[frm.LabID.options.selectedIndex].value!='-'){\n");
			out.write("var aArray = eval(\"a\"+frm.LabID.options[frm.LabID.options.selectedIndex].value);\n");
			out.write("frm.SectID.options.length = aArray.length + 1;\n");
			out.write("for(i = 0;i<aArray.length;i++){\n");
			out.write("frm.SectID.options[i+1].value = aArray[i][0];\n");
			out.write("frm.SectID.options[i+1].text = aArray[i][1];\n");
			out.write("}\n} else {\n");
			out.write("frm.SectID.options.length = 1;\n");
			out.write("}\nfrm.SectID.options.selectedIndex = 0;\n}\n");
			out.write("</script>\n");										

			out.write("<tr><td class='heading'>Laboratory</td><td class='smallheading'>Name</td><td>");
			cd = new ComboDescriptor("Lab_View", "Lab_ID", "Lab_Name");
			cd.name = "LabID";
			cd.prompt = "-- Choose --";
			cd.selected = lab;
			cd.orderBy = "Lab_Name";
			cd.selectDistinct = true;
			cd.tagParams = "onChange='swapSection(this.form)'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write("</td></tr>\n");
			out.write("<tr><td></td><td class='smallheading'>Code</td><td><select name='SectID'><option value='-' selected>-- Choose --</option></select></td></tr>\n");
			out.write("<tr><td></td><td class='smallheading'>Number</td><td><input type='text' name='LabNum' size='20' value='" + FREDUtils.noNulls(getField(LAB_NUMBER)) + "'></td><td></td></tr>\n");
			if (getField(LAB_SECTION) != null) {
				out.write("<script language='JavaScript'>\n");
				out.write("swapSection(form1);");
				out.write("for(i=0;i<form1.SectID.options.length;i++){ if (form1.SectID.options[i].value=='" + getField(LAB_SECTION) + "') { form1.SectID.options.selectedIndex = i; }}\n");
				out.write("</script>\n");
			}
			out.write(
				"<tr><td class='heading' colspan='2'>Collection Comments</td><td><textarea name='CollComm' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getField(COLLECTION_COMMENTS))
					+ "</textarea></td></tr>\n");			
			super.makeEndBitHTML(out);
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate(
					"DELETE FROM Paleontology WHERE Record_ID = "
						+ record.getRecordID());
				//Create PALEONTOLOGY entry
				conn.executeUpdate(
					"INSERT INTO Paleontology (Record_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments) VALUES ("
						+ record.getRecordID()
						+ ((identDate != null)
							? ", TO_DATE('"
								+ identDate.getDateString()
								+ "'), "
								+ JspUtils.sqlEscape(identDate.getDateRounding())
							: ", NULL, NULL")
						+ ", "
						+ JspUtils.sqlEscape(
							DataEntryUtils.getStageID(
								getField(IDT_AGE_START),
								getField(IDT_START_MOD),
								getField(IDT_AGE_STOP),
								getField(IDT_STOP_MOD),
								state))
						+ ", "
						+ JspUtils.sqlEscape(getField(STAGE_COMMENTS))
						+ ", "
						+ JspUtils.sqlEscape(getField(LAB_SECTION))
						+ ", "
						+ JspUtils.sqlEscape(getField(LAB_NUMBER))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLLECTION_COMMENTS))
						+ ")");
				//Create IDENTIFIERS entries
				if (identifiers != null) {
					for (Iterator i = identifiers.iterator(); i.hasNext();) {
						conn.executeUpdate(
							"INSERT INTO Identifier (Record_ID, Person_ID) VALUES ("
								+ record.getRecordID()
								+ ", "
								+ (Integer) i.next()
								+ ")");
					}
				}
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					record = (PaleontologyRecord) PaleontologyRecord.getData(record.getRecordID(), user, state, true);
					sample = new Sample(sample.getSampleID(), user, state, true);
				} catch (Exception e) {
				}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException();
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException();
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InvalidCredentialsException();
			}

		}
		return record.getRecordID();
	}

	protected void checkMandatoryFields() throws DataInputException {
	}

}
