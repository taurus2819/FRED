package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class DrillholeLocalityDE extends LocalityDE {

	private String personID;
	private RoundedDate spudDate;
	private RoundedDate compDate;

	public DrillholeLocalityDE(User user, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, folderID, "Drillhole", state);
	}

	public DrillholeLocalityDE(int id, User user, PageState state)
		throws
			IOException,
			SQLException,
			DataInputException,
			InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals("Drillhole"))
			throw new DataInputException("Feature Type", "Invalid");
		setField(OPERATING_COMPANY, sample.getAsString(Sample.PERSON));
		setField(
			SPUD_DATE,
			DataEntryUtils.reverseParseDate(
				sample.getAsDate(Sample.START_DATE),
				sample.getAsString(Sample.START_DATE_ROUNDING)));
		setField(
			COMPLETION_DATE,
			DataEntryUtils.reverseParseDate(
				sample.getAsDate(Sample.FINISH_DATE),
				sample.getAsString(Sample.FINISH_DATE_ROUNDING)));
		setField(
			LICENCE_AREA,
			sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME));
		setField(DATUM_TYPE, sample.getAsString(Sample.DATUM_TYPE));
		setField(DATUM_ELEVATION, sample.getAsString(Sample.DATUM_ELEVATION));
		setField(KICK_OFF_DEPTH, sample.getAsString(Sample.START_DEPTH));
		setField(TERMINATION_DEPTH, sample.getAsString(Sample.FINISH_DEPTH));
		savedFlag = true;
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case OPERATING_COMPANY :
					rs =
						conn.executeQuery(
							"SELECT Person_ID FROM Person_View WHERE Name = "
								+ JspUtils.sqlEscape(value.trim()));
					if (!rs.next())
						throw new DataInputException(
							"Operating Company",
							"Invalid value");
					personID = rs.getString(1);
					break;
				case SPUD_DATE :
					spudDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case COMPLETION_DATE :
					compDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case DATUM_TYPE :
					if (!(value.equals("RT") || value.equals("KB") || value.equals("Seafloor")))
						throw new DataInputException(
							"Datum Type",
							"Invalid Data");
					break;
				case DATUM_ELEVATION :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException(
							"Datum Elevation",
							"Invalid Data");
					break;
				case KICK_OFF_DEPTH :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException(
							"Kick-off Depth",
							"Invalid Data");
					break;
				case TERMINATION_DEPTH :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException(
							"Termination Depth",
							"Invalid Data");
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();
		}
	}

	protected void resetHiddenField(int field) {
		switch (field) {
			case OPERATING_COMPANY :
				personID = null;
				break;
			case SPUD_DATE :
				spudDate = null;
				break;
			case COMPLETION_DATE :
				compDate = null;
				break;
		}
	}

	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
		out.write("<table border='0' cellspacing='0' cellpadding='2'>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Drillhole Name</td><td><input type='text' name='FeatName' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DRILLHOLE_NAME))
				+ "'></td></tr>\n");
		super.makeDataEntryHTML(out);
		out.write(
			"<tr><td class='heading'>Operating Company</td><td></td><td><input type='text' name='Person' value='"
				+ FREDUtils.noNulls(getFieldForHTML(OPERATING_COMPANY))
				+ "' size='40'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=OpComp\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Drilling Dates</td><td class='smallheading'>Spud Date</td><td><input type='text' name='StartDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(SPUD_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=StartDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'></td><td class='smallheading'>Completion Date</td><td><input type='text' name='FinishDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COMPLETION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=FinishDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Licence Area</td><td></td><td><input type='text' name='LicArea' value='"
				+ FREDUtils.noNulls(getFieldForHTML(LICENCE_AREA))
				+ "' size='40'></td></tr>");
		out.write("<tr><td class='heading'>Datum Elevation</td><td></td>");
		out.write(
			"<td class='smallheading'><select name='DatumType'><option value='-'"
				+ ((getFieldForHTML(DATUM_TYPE) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='RT'"
				+ ((getFieldForHTML(DATUM_TYPE) != null
					&& getFieldForHTML(DATUM_TYPE).equals("RT"))
					? " selected"
					: "")
				+ ">RT</option><option value='KB'"
				+ ((getFieldForHTML(DATUM_TYPE) != null
					&& getFieldForHTML(DATUM_TYPE).equals("KB"))
					? " selected"
					: "")
				+ ">KB</option><option value='Seafloor'"
				+ ((getFieldForHTML(DATUM_TYPE) != null
					&& getFieldForHTML(DATUM_TYPE).equals("Seafloor"))
					? " selected"
					: "")
				+ ">Seafloor</option></select>&nbsp;&nbsp;");
		out.write(
			"<input type='text' name='DatumEl' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DATUM_ELEVATION))
				+ "' size='10'>&nbsp;m&nbsp;asl</td></tr>\n");
		out.write(
			"<tr><td class='heading'>Drillhole Depths</td><td class='smallheading'>Kick-off</td><td class='smallheading'><input type='text' name='StartDepth' value='"
				+ FREDUtils.noNulls(getFieldForHTML(KICK_OFF_DEPTH))
				+ "'>&nbsp;m</td></tr>\n");
		out.write(
			"<tr><td class='heading'></td><td class='smallheading'>Termination (TD)</td><td class='smallheading'><input type='text' name='FinishDepth' value='"
				+ FREDUtils.noNulls(getFieldForHTML(TERMINATION_DEPTH))
				+ "'>&nbsp;m</td></tr>\n");
		out.write("</table>\n");
		super.makeEndBitHTML(out);
	}

	public int save()
		throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate(
					"UPDATE Feature SET Person_ID = "
						+ JspUtils.sqlEscape(personID)
						+ ((spudDate != null)
							? ", Start_Date = TO_DATE('"
								+ spudDate.getDateString()
								+ "'), Start_Date_Rounding = "
								+ JspUtils.sqlEscape(spudDate.getDateRounding())
							: ", Start_Date = NULL, Start_Date_Rounding = NULL")
						+ ((compDate != null)
							? ", Finish_Date = TO_DATE('"
								+ compDate.getDateString()
								+ "'), Finish_Date_Rounding = "
								+ JspUtils.sqlEscape(compDate.getDateRounding())
							: ", Finish_Date = NULL, Finish_Date_Rounding = NULL")
						+ ", Drillhole_Licence_Name = "
						+ JspUtils.sqlEscape(fields[LICENCE_AREA])
						+ ", Datum_Type = "
						+ JspUtils.sqlEscape(fields[DATUM_TYPE])
						+ ", Datum_Elevation = "
						+ JspUtils.sqlEscape(fields[DATUM_ELEVATION])
						+ ", Start_Depth = "
						+ JspUtils.sqlEscape(fields[KICK_OFF_DEPTH])
						+ ", Finish_Depth = "
						+ JspUtils.sqlEscape(fields[TERMINATION_DEPTH])
						+ " WHERE Feature_ID = "
						+ feature.getFeatureID());
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				feature = new Feature(feature.getFeatureID(), user, state, true);
				if (feature.getSampleCount() == 0) {
					conn.executeUpdate("INSERT INTO Sample (Feature_ID, Audit_ID) VALUES (" + feature.getFeatureID() + ", " + feature.getAsString(Feature.AUDIT_ID) + ")");
					feature = new Feature(feature.getFeatureID(), user, state, true);
				}
				int sampleID = ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
				sample = new Sample(sampleID, user, state, true);
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
		return feature.getFeatureID();
	}

}
