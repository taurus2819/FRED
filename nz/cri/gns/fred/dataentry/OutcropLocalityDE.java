package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class OutcropLocalityDE extends LocalityDE {

	private SampleDE sampleDE;

	public OutcropLocalityDE(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, folderID, Feature.OUTCROP_LOCALITY, state);
		sampleDE = new SampleDE(user, folderID, state);
		sampleDE.setOutcropSamp(true);
	}
	
	public OutcropLocalityDE(int featureID, User user, PageState state) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException  {
		super(featureID, user, state);
		if (!featureType.equals(Feature.OUTCROP_LOCALITY))
			throw new DataInputException("Feature Type", "Invalid");
		getFromDatabase(sample);
		savedFlag = true;
		sampleDE = new SampleDE(sample.getSampleID(), user, state);
		sampleDE.setOutcropSamp(true);
	}

	public void copyFrom(int featureID) throws DataInputException, InsufficientPrivelegesException, SQLException, IOException {
		super.copyFrom(featureID);
		Feature copyFeature = new Feature(featureID, user, state);
		int copySampleID = ((Integer) copyFeature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
		sampleDE.copyFrom(copySampleID);
	}

	public void setField(int field, String value) throws DataInputException {
		if (field < 30) {
			super.setField(field, value);
		} else {
			try {
				sampleDE.setField(field, value);
			} catch (TaxonomicListException e) {}
		}
		savedFlag = false;
	}

	public String getField(int field) {
		if (field < 30) {
			return super.getField(field);
		} else {
			return sampleDE.getField(field); 
		}
	}

	public void setTempField(int field, String value) {
		if (field < 30) {
			super.setTempField(field, value);
		} else {
			sampleDE.setTempField(field, value);
		}
	}

	public String getTempField(int field) {
		if (field < 30) {
			return super.getTempField(field);
		} else {
			return sampleDE.getTempField(field);
		}
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		super.makeDataEntryHTML(out);
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
		sampleDE.makeDataEntryHTML(out);
		super.makeEndBitHTML(out);
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		super.makeExcelImportHTML(out);
		sampleDE.makeExcelImportHTML(out);
		out.write("</tr>\n");
	}
	
	public int save() throws SQLException, IOException, InsufficientPrivelegesException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				sampleDE.setFeatureID(feature.getFeatureID());
				sampleDE.setAuditID(feature.getAsInt(Feature.AUDIT_ID));
				sampleDE.save();
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException(e.getMessage());
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException(e.getMessage());
			} catch (InsufficientPrivelegesException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InsufficientPrivelegesException();
			}
		}
		return feature.getFeatureID();
	}
	
	public int submit() throws SQLException, IOException, DataInputException, InsufficientPrivelegesException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		conn.getConnection().setAutoCommit(false);
		try {
			super.submit();
			sampleDE.setFeatureID(feature.getFeatureID());
			sampleDE.setAuditID(feature.getAsInt(Feature.AUDIT_ID));
			sampleDE.submit();
			conn.getConnection().commit();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
		} catch (SQLException e) {
			conn.getConnection().rollback();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
			throw e;
		} catch (IOException e) {
			conn.getConnection().rollback();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
			throw e;
		} catch (InsufficientPrivelegesException e) {
			conn.getConnection().rollback();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
			throw e;
		} catch (DataInputException e) {
			conn.getConnection().rollback();
			conn.getConnection().setAutoCommit(true);
			conn.releaseStatement();
			throw e;
		}
		return feature.getFeatureID();	
	}
	
}
