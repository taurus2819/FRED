package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class DrillholeLocalityDE extends LocalityDE {

	private String personID;
	private RoundedDate spudDate;
	private RoundedDate compDate;

	public DrillholeLocalityDE(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, Feature.DRILLHOLE_LOCALITY, state);
	}

	public DrillholeLocalityDE(int id, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		super(id, user, state);
		if (!featureType.equals(Feature.DRILLHOLE_LOCALITY))
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
					String query = "SELECT Person_ID FROM Person_View WHERE Name = ?";
					rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {value.trim()});
					if (!rs.next())
						throw new DataInputException("Operating Company", "Invalid value");
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
						throw new DataInputException("Datum Type", "Invalid Data");
					break;
				case DATUM_ELEVATION :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException("Datum Elevation",	"Invalid Data");
					break;
				case KICK_OFF_DEPTH :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException("Kick-off Depth", "Invalid Data");
					break;
				case TERMINATION_DEPTH :
					if (!FREDUtils.isNumeric(value))
						throw new DataInputException("Termination Depth", "Invalid Data");
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
		out.write("<tr><td class='heading' colspan='2'>Drillhole Name</td><td><input type='text' name='FeatName' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DRILLHOLE_NAME))
				+ "'></td></tr>\n");
		super.makeDataEntryHTML(out);
		out.write("<tr><td class='heading'>Operating Company</td><td></td><td><input type='text' name='Person' value='"
				+ FREDUtils.noNulls(getFieldForHTML(OPERATING_COMPANY))
				+ "' size='40'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=OpComp\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Drilling Dates</td><td class='smallheading'>Spud Date</td><td><input type='text' name='StartDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(SPUD_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=StartDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'></td><td class='smallheading'>Completion Date</td><td><input type='text' name='FinishDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COMPLETION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=FinishDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Licence Area</td><td></td><td><input type='text' name='LicArea' value='"
				+ FREDUtils.noNulls(getFieldForHTML(LICENCE_AREA))
				+ "' size='40'></td></tr>");
		out.write("<tr><td class='heading'>Datum Elevation</td><td></td>");
		out.write("<td class='smallheading'><select name='DatumType'><option value='-'"
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
		out.write("<input type='text' name='DatumEl' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DATUM_ELEVATION))
				+ "' size='10'>&nbsp;m&nbsp;asl</td></tr>\n");
		out.write("<tr><td class='heading'>Drillhole Depths</td><td class='smallheading'>Kick-off</td><td class='smallheading'><input type='text' name='StartDepth' value='"
				+ FREDUtils.noNulls(getFieldForHTML(KICK_OFF_DEPTH))
				+ "'>&nbsp;m</td></tr>\n");
		out.write("<tr><td class='heading'></td><td class='smallheading'>Termination (TD)</td><td class='smallheading'><input type='text' name='FinishDepth' value='"
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
				QueryDescriptor qd = new QueryDescriptor("feature");
				qd.addQueryColumn("person_id", Types.NUMERIC, ((personID != null) ? new Integer(personID) : null));
				qd.addQueryColumn("start_date", Types.DATE ,((spudDate != null) ? spudDate.getDate() : null));
				qd.addQueryColumn("start_date_rounding", Types.VARCHAR, ((spudDate != null) ? spudDate.getDateRounding() : null));
				qd.addQueryColumn("finish_date", Types.DATE ,((compDate != null) ? compDate.getDate() : null));
				qd.addQueryColumn("finish_date_rounding", Types.VARCHAR, ((compDate != null) ? compDate.getDateRounding() : null));
				qd.addQueryColumn("drillhole_licence_name", Types.VARCHAR, fields[LICENCE_AREA]);
				qd.addQueryColumn("datum_type", Types.VARCHAR, fields[DATUM_TYPE]);
				qd.addQueryColumn("datum_elevation", Types.NUMERIC, ((fields[DATUM_ELEVATION] != null) ? new Double(fields[DATUM_ELEVATION]) : null));
				qd.addQueryColumn("start_depth", Types.NUMERIC, ((fields[KICK_OFF_DEPTH] != null) ? new Double(fields[KICK_OFF_DEPTH]) : null));
				qd.addQueryColumn("finish_depth", Types.NUMERIC, ((fields[TERMINATION_DEPTH] != null) ? new Double(fields[TERMINATION_DEPTH]) : null));
				qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getFeatureID()));
				DBUtils.doUpdate(qd, "feature_id = ?", conn);
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				feature = new Feature(feature.getFeatureID(), user, state, true);
				if (feature.getSampleCount() == 0) {
					qd = new QueryDescriptor("sample");
					qd.addQueryColumn("feature_id", Types.NUMERIC, new Integer(feature.getFeatureID()));
					qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
					DBUtils.doInsertUsingSequence(qd, "sample_id", "sample_seq", conn, false);
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
