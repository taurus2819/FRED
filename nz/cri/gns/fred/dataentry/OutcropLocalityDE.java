package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class OutcropLocalityDE extends LocalityDE {

	private SampPropRecordDE sampPropRecordDE;

	public OutcropLocalityDE(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, "Outcrop", state);
		sampPropRecordDE = new SampPropRecordDE(user, folderID, state);
		sampPropRecordDE.setOutcropSamp(true);
	}
	
	public OutcropLocalityDE(int id, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException  {
		super(id, user, state);
		if (!featureType.equals("Outcrop")) throw new DataInputException("Feature Type", "Invalid");
		if (sample.get(Sample.WORKING_RECORDS) != null) {
			for (Iterator i = sample.getAsVector(Sample.WORKING_RECORDS).iterator(); i.hasNext(); ) {
				KeyValueObject key = (KeyValueObject) i.next();
				if (key.getValue().equals("SMP")) {
					try {
						sampPropRecordDE = new SampPropRecordDE(Integer.parseInt(key.getKey()), user, state);
					} catch (Exception e) {}
				}
			}
		}
		if (sampPropRecordDE == null) {
			sampPropRecordDE = new SampPropRecordDE(user, sample.getSampleID(), sample.getAsInt(Sample.WORKING_FOLDER_ID), state);
		}
		sampPropRecordDE.setOutcropSamp(true);
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		out.write("<table border='0' cellspacing='0' cellpadding='2'>\n");
		out.write("<tr><td class='heading' colspan='2'>Field Number</td><td><input type='text' name='FeatName' value='" + FREDUtils.noNulls(getField(LocalityDE.FIELD_NUMBER)) + "'></td></tr>\n");
		super.makeDataEntryHTML(out);
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
		sampPropRecordDE.makeDataEntryHTML(out);
		super.makeEndBitHTML(out);
	}

	public int save() throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				sampPropRecordDE.setSample(sample);
				sampPropRecordDE.save();
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
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
