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
import nz.cri.gns.fred.data.AdoptionRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class AdoptionRecordDE extends RecordDE {

	private RoundedDate adoDate;
	private Vector adoptors;

	public AdoptionRecordDE(User user, int sampleID, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "ADO", state);
	}

	public AdoptionRecordDE(User user, int folderID, PageState state)
		throws DataInputException, SQLException, IOException {
		super(user, folderID, "ADO", state);
	}

	public AdoptionRecordDE(int recID, User user, PageState state)
		throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, "ADO", user, state);
		try {
			setField(ADOPTION_DATE,
				DataEntryUtils.reverseParseDate(
					record.getAsDate(Record.ADOPTION_DATE),
					record.getAsString(Record.ADOPTION_DATE_ROUNDING)));
			if (record.get(Record.ADOPTOR) != null) {
				StringBuffer adoptName = new StringBuffer();
				for (Iterator i = record.getAsVector(Record.ADOPTOR).iterator(); i.hasNext();) {
					KeyValueObject adopt = (KeyValueObject) i.next();
					adoptName.append(adopt.getValue() + "\n");
				}
				setField(ADOPTORS, adoptName.toString());
			}
			setField(ADO_AGE_START, record.getAsString(Record.ADOPTED_STAGE_LOWER_ID));
			setField(ADO_START_MOD, record.getAsString(Record.ADOPTED_STAGE_LOWER_MOD));
			setField(ADO_AGE_STOP, record.getAsString(Record.ADOPTED_STAGE_UPPER_ID));
			setField(ADO_STOP_MOD, record.getAsString(Record.ADOPTED_STAGE_UPPER_MOD));
			setField(ADO_COMMENTS, record.getAsString(Record.COMMENTS));
		} catch (TaxonomicListException e) {}
	}

	protected void parseField(int field, String value)
		throws DataInputException, TaxonomicListException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case ADOPTION_DATE :
					adoDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case ADOPTORS :
					adoptors = new Vector();
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
							adoptors.add(new Integer(rs.getInt(1)));
						} catch (Exception e) {
							throw new DataInputException(
								"Adoptor",
								value.substring(0, value.indexOf("\n")).trim()
									+ " not in database - add through builder");
						}
						value =
							value.substring(
								value.indexOf("\n") + 1,
								value.length());
					}
					break;
				case ADO_AGE_START :
					DataEntryUtils.parseAge(value, getField(ADO_AGE_STOP), "Adopted Age", state);
					break;
				case ADO_AGE_STOP :
					DataEntryUtils.parseAge(getField(ADO_AGE_START), value, "Adopted Age", state);
					break;
				case ADO_START_MOD :
				case ADO_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException(
							"Adopted Age",
							"Bad Modifier");
					break;
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
				adoDate = null;
				break;
			case ADOPTORS :
				adoptors = null;
				break;
		}
	}

	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		super.makeDataEntryHTML(out);
		out.write(
			"<tr><td class='heading'>Adoption Date</td><td></td><td><input type='text' name='AdoDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(ADOPTION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=AdoDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Adoptors</td><td></td><td><textarea name='Adoptor' cols='40' rows='2'>"
				+ FREDUtils.noNulls(getFieldForHTML(ADOPTORS))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Adoptor\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Stage Limits</td><td class='smallheading'>Adopted</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "StageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(ADO_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td><select name='StartMod'><option value='-' "
				+ ((getFieldForHTML(ADO_START_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(ADO_START_MOD) != null
					&& getFieldForHTML(ADO_START_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "StageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(ADO_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td class='heading'><select name='StopMod'><option value='-' "
				+ ((getFieldForHTML(ADO_STOP_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(ADO_STOP_MOD) != null
					&& getFieldForHTML(ADO_STOP_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Comments</td><td><textarea name='Comm' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(ADO_COMMENTS))
				+ "</textarea></td></tr>\n");				
		super.makeEndBitHTML(out);
	}

	public String getAdoDate() {
		return adoDate.getDateString();
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate("DELETE FROM Adoption WHERE Record_ID = " + record.getRecordID());
				String stageID = DataEntryUtils.getStageID(getField(ADO_AGE_START), getField(ADO_START_MOD), getField(ADO_AGE_STOP), getField(ADO_STOP_MOD), state);
				//Create ADOPTION entry
				conn.executeUpdate(
					"INSERT INTO Adoption (Record_ID, Adoption_Date, Date_Rounding, Adopted_Stage_ID, Comments) VALUES ("
						+ record.getRecordID()
						+ ((adoDate != null) ? ", TO_DATE('" + adoDate.getDateString() + "'), " + JspUtils.sqlEscape(adoDate.getDateRounding())
							: ", NULL, NULL")
						+ ", "
						+ JspUtils.sqlEscape(stageID)
						+ ", "
						+ JspUtils.sqlEscape(getField(ADO_COMMENTS))
						+ ")");
				//Create ADOPTORS entries
				if (adoptors != null) {
					for (Iterator i = adoptors.iterator(); i.hasNext();) {
						conn.executeUpdate(
							"INSERT INTO Adoptor (Record_ID, Person_ID) VALUES ("
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
					record = (AdoptionRecord) AdoptionRecord.getData(record.getRecordID(), user, state, true);
					sample = new Sample(sample.getSampleID(), user, state, true);
				} catch (Exception e) {}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			}

		}
		return record.getRecordID();
	}

}
