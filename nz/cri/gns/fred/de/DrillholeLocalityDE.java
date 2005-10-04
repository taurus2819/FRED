package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class DrillholeLocalityDE extends LocalityDE {

	public DrillholeLocalityDE(User user, int folderID, DAOFactory factory, ContentProvider provider) throws SQLException, IOException, DataInputException, StorageAccessException, InsufficientPrivelegesException {
		this (user, folderID, factory, provider, FREDConstants.DRILLHOLE);
	}
	
	protected DrillholeLocalityDE(User user, int folderID, DAOFactory factory, ContentProvider provider, String localityType) throws StorageAccessException, InsufficientPrivelegesException {
		super(user, folderID, localityType, factory, provider);
	}

	public DrillholeLocalityDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
		this (feature, folderID, user, factory, provider, FREDConstants.DRILLHOLE);
	}

	protected DrillholeLocalityDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider, String localityType) throws InsufficientPrivelegesException, StorageAccessException, DataInputException {
		super(feature, folderID, user, factory, provider);
		if (!feature.getFeatureType().equals(localityType))
			throw new DataInputException("Feature Type", "Invalid");
	}

	protected void getFromDatabase(Feature fromFeature) throws InsufficientPrivelegesException {
		super.getFromDatabase(feature);
		//set drillhole or vs fields fields
		feature.setPerson(fromFeature.getPerson());
		feature.setStartDate(fromFeature.getStartDate());
		feature.setStartDateRounding(fromFeature.getStartDateRounding());
		feature.setFinishDate(fromFeature.getFinishDate());
		feature.setFinishDateRounding(fromFeature.getFinishDateRounding());
		feature.setDrillholeLicenceName(fromFeature.getDrillholeLicenceName());
		feature.setDatumType(fromFeature.getDatumType());
		feature.setDatumElevation(fromFeature.getDatumElevation());
		feature.setStartDepth(fromFeature.getStartDepth());
		feature.setFinishDepth(fromFeature.getFinishDepth());
	}

	public void updateFromRequest(HttpServletRequest request) throws DataInputException {
		super.updateFromRequest(request);
		
		String[] error = null;
		
		//Operator
		String operator = request.getParameter("Person").trim();
		//Will always be a company
		if (operator.length() == 0)
			feature.setPerson(null);
		else if (feature.getPerson() == null || !feature.getPerson().getFamilyName().equals(operator)) try {
			feature.setPerson(new PersonUtil(factory).findOrCreateCompany(operator));
		} catch (StorageAccessException e) {
			error = new String[] {"Operating Company", "Error storing the operator's name"};
		}
		
		try {
			String startDate = request.getParameter("StartDate");
			feature.setStartDate(FREDUtil.parseDateFromDE(startDate));
			feature.setStartDateRounding(FREDUtil.parseDateRoundingFromDE(startDate));
		} catch (ParseException e) {
			error = new String[] {"Start Date", "Badly formatted date"};
		}

		try {
			String finishDate = request.getParameter("FinishDate");
			feature.setStartDate(FREDUtil.parseDateFromDE(finishDate));
			feature.setStartDateRounding(FREDUtil.parseDateRoundingFromDE(finishDate));
		} catch (ParseException e) {
			error = new String[] {"Start Date", "Badly formatted date"};
		}
		
		feature.setDrillholeLicenceName(request.getParameter("LicArea"));
		
		String datumType = request.getParameter("DatumType");
		if (datumType.equals("-"))
			feature.setDatumType(null);
		else
			feature.setDatumType(datumType);
		
		String datumElevation = request.getParameter("DatumEl").trim();
		if (datumElevation.length() == 0)
			feature.setDatumElevation(null);
		else try {
			feature.setDatumElevation(new Double(datumElevation));
		} catch (NumberFormatException e) {
			error = new String[] {"Datum Elevation", "Invalid elevation entered"};
		}
		
		String startDepth = request.getParameter("StartDepth").trim();
		if (startDepth.length() == 0)
			feature.setStartDepth(null);
		else try {
			feature.setStartDepth(new Double(startDepth));
		} catch (NumberFormatException e) {
			error = new String[] {"Kickoff Depth", "Invalid depth entered"};
		}

		String finishDepth = request.getParameter("FinishDepth").trim();
		if (finishDepth.length() == 0)
			feature.setFinishDepth(null);
		else try {
			feature.setFinishDepth(new Double(finishDepth));
		} catch (NumberFormatException e) {
			error = new String[] {"Termination Depth", "Invalid depth entered"};
		}
		
		if (error != null)
			throw new DataInputException(error[0], error[1]);
	}

	public void makeDataEntryHTML(PrintWriter out) throws IOException, SQLException {
		super.makeDataEntryHTML(out);
	
		loadTemplate(provider.getContent(getContentPrefix() + ".de.form"), out);
		
		super.makeEndBitHTML(out);
	}

	protected void loadTemplate(Template template, PrintWriter out) throws IOException {
		if (feature.getPerson() != null)
			template.addSub("Person", feature.getPerson().getDisplayName());
		if (feature.getStartDate() != null)
			template.addSub("StartDate", FREDUtil.formatDateForDE(feature.getStartDate(), feature.getStartDateRounding()));
		if (feature.getFinishDate() != null)
			template.addSub("FinishDate", FREDUtil.formatDateForDE(feature.getFinishDate(), feature.getFinishDateRounding()));
		if (feature.getDrillholeLicenceName() != null)
			template.addSub("licenseArea", feature.getDrillholeLicenceName());
		
		if (feature.getDatumType() != null) {
			template.addSub("is" + feature.getDatumType(), "yes");
			template.addSub("datumType", feature.getDatumType());
		}
		if (feature.getDatumElevation() != null)
			template.addSub("datumElevation", feature.getDatumElevation().toString());
		
		if (feature.getStartDepth() != null) 
			template.addSub("startDepth", feature.getStartDate().toString());
		if (feature.getFinishDepth() != null) 
			template.addSub("finishDepth", feature.getFinishDate().toString());
		
		template.loadAll(out);
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		super.makeExcelImportHTML(out);
		PrintWriter pw = new PrintWriter(out);
		loadTemplate(provider.getContent(getContentPrefix() + ".de.excel"), pw);
		pw.flush();
	}
	
	protected String getContentPrefix() {
		return "drillhole";
	}

	/*
	 * Doesn't need to be overridden - LocalityDE version is fine. 
	public int save() throws SQLException, IOException, InsufficientPrivelegesException {
		//Piggyback the localityDE's save
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
			} catch (InsufficientPrivelegesException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			}
		}
		return feature.getFeatureID();
	}
*/
	public boolean usesCalendar() {
		return true;
	}
	
	public void makePostFormHTML(PrintWriter out) throws IOException {
		Template template = provider.getContent("calendar.script");
		template.addSub("inputField", "StartDate");
		template.loadAll(out);
		template = provider.getContent("calendar.script");
		template.addSub("inputField", "FinishDate");
		template.loadAll(out);
	}

	public String getHeading() {
		return "Edit drillhole locality";
	}

}
