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

public class VertSectLocalityDE extends LocalityDE {

	private String personID;
	private RoundedDate startDate;
	private RoundedDate compDate;

	public VertSectLocalityDE(User user, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, folderID, "Vertical Section", state);
	}

	public VertSectLocalityDE(int id, User user, PageState state)
		throws
			IOException,
			SQLException,
			DataInputException,
			InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals("Vertical Section"))
			throw new DataInputException("Feature Type", "Invalid");
		setField(SECTION_COLLECTOR, sample.getAsString(Sample.PERSON));
		setField(
			START_DATE,
			DataEntryUtils.reverseParseDate(
				sample.getAsDate(Sample.START_DATE),
				sample.getAsString(Sample.START_DATE_ROUNDING)));
		setField(
			COMPLETION_DATE,
			DataEntryUtils.reverseParseDate(
				sample.getAsDate(Sample.FINISH_DATE),
				sample.getAsString(Sample.FINISH_DATE_ROUNDING)));
		setField(DATUM_TYPE, sample.getAsString(Sample.DATUM_TYPE));
		setField(DATUM_ELEVATION, sample.getAsString(Sample.DATUM_ELEVATION));
		setField(TOP_HORIZON, sample.getAsString(Sample.START_DEPTH));
		setField(BASE_HORIZON, sample.getAsString(Sample.FINISH_DEPTH));
		savedFlag = true;
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case SECTION_COLLECTOR :
					rs =
						conn.executeQuery(
							"SELECT Person_ID FROM Person_View WHERE Name = "
								+ JspUtils.sqlEscape(value.trim()));
					if (!rs.next())
						throw new DataInputException(
							"Section Collector",
							"Invalid value");
					personID = rs.getString(1);
					break;
				case START_DATE :
					startDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case COMPLETION_DATE :
					compDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case DATUM_TYPE :
					if (!(value.equals("Top") || value.equals("Bottom")))
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
				case TOP_HORIZON :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException(
							"Top Horizon",
							"Invalid Data");
					break;
				case BASE_HORIZON :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException(
							"Base Horizon",
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
			case SECTION_COLLECTOR :
				personID = null;
				break;
			case START_DATE :
				startDate = null;
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
			"<tr><td class='heading' colspan='2'>Section Name</td><td><input type='text' name='FeatName' value='"
				+ FREDUtils.noNulls(getField(DRILLHOLE_NAME))
				+ "'></td></tr>\n");
		super.makeDataEntryHTML(out);
		out.write(
			"<tr><td class='heading'>Section Collector</td><td></td><td><input type='text' name='Person' value='"
				+ FREDUtils.noNulls(getField(OPERATING_COMPANY))
				+ "' size='40'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=OpComp\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Sampling Dates</td><td class='smallheading'>Start Date</td><td><input type='text' name='StartDate' value='"
				+ FREDUtils.noNulls(getField(SPUD_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=StartDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'></td><td class='smallheading'>Completion Date</td><td><input type='text' name='FinishDate' value='"
				+ FREDUtils.noNulls(getField(COMPLETION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=FinishDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Datum Elevation</td><td></td>");
		out.write(
			"<td class='smallheading'><select name='DatumType'><option value='-'"
				+ ((getField(DATUM_TYPE) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Top'"
				+ ((getField(DATUM_TYPE) != null
					&& getField(DATUM_TYPE).equals("Top"))
					? " selected"
					: "")
				+ ">Top</option><option value='Bottom'"
				+ ((getField(DATUM_TYPE) != null
					&& getField(DATUM_TYPE).equals("Bottom"))
					? " selected"
					: "")
				+ ">Bottom</option></select>&nbsp;&nbsp;");
		out.write(
			"<input type='text' name='DatumEl' value='"
				+ FREDUtils.noNulls(getField(DATUM_ELEVATION))
				+ "' size='10'>&nbsp;m&nbsp;asl</td></tr>\n");
		out.write(
			"<tr><td class='heading'>Section Heights</td><td class='smallheading'>Top Horizon</td><td class='smallheading'><input type='text' name='StartDepth' value='"
				+ FREDUtils.noNulls(getField(KICK_OFF_DEPTH))
				+ "'>&nbsp;m</td></tr>\n");
		out.write(
			"<tr><td class='heading'></td><td class='smallheading'>Base Horizon</td><td class='smallheading'><input type='text' name='FinishDepth' value='"
				+ FREDUtils.noNulls(getField(TERMINATION_DEPTH))
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
						+ ((startDate != null)
							? ", Start_Date = TO_DATE('"
								+ startDate.getDateString()
								+ "'), Start_Date_Rounding = "
								+ JspUtils.sqlEscape(startDate.getDateRounding())
							: ", Start_Date = NULL, Start_Date_Rounding = NULL")
						+ ((compDate != null)
							? ", Finish_Date = TO_DATE('"
								+ compDate.getDateString()
								+ "'), Finish_Date_Rounding = "
								+ JspUtils.sqlEscape(compDate.getDateRounding())
							: ", Finish_Date = NULL, Finish_Date_Rounding = NULL")
						+ ", Datum_Type = "
						+ JspUtils.sqlEscape(fields[DATUM_TYPE])
						+ ", Datum_Elevation = "
						+ JspUtils.sqlEscape(fields[DATUM_ELEVATION])
						+ ", Start_Depth = "
						+ JspUtils.sqlEscape(fields[TOP_HORIZON])
						+ ", Finish_Depth = "
						+ JspUtils.sqlEscape(fields[BASE_HORIZON])
						+ " WHERE Feature_ID = "
						+ feature.getFeatureID());
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				feature = new Feature(feature.getFeatureID(), user, state, true);
				int sampleID = ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
				sample = new Sample(sampleID, user, state, true);
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
		return feature.getFeatureID();
	}

}
