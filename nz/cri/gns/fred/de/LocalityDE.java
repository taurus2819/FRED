package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.site.DatumMethod;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.IconnedLink;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.NZGD49;
import nz.cri.gns.util.map.WGS84;
import nz.cri.gns.util.map.Datum.MapSheetCoordinate;

public abstract class LocalityDE extends DETemplate implements DataEntryForm {

	public static final String comboNull = "-";
	protected DAOFactory factory;
	protected FeatureUtil featureUtil;
	protected ContentProvider provider;
	
	protected User user;
	private UserFolder workingFolder;
	
	protected Feature feature;
	/**
	 * Temporary storage for working comments
	 */
	protected String editComments;	
	private SiteRecord site;
	/** This allows for bad coordinates to still be re-editted */
	private Datum.Coordinate coord;
	private Datum datum;
	
	private boolean isAllowedSubmit;
	
	
	public LocalityDE(User user, int folderID, String featureType, DAOFactory factory, ContentProvider content)	throws StorageAccessException, InsufficientPrivelegesException {
		initialise((featureUtil = new FeatureUtil(factory)).createFeature(folderID, featureType), folderID, user, factory, content);
	}

	public LocalityDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider content) throws InsufficientPrivelegesException, StorageAccessException {
        featureUtil = new FeatureUtil(factory);
		initialise(feature, folderID, user, factory, content);
		try {
			site = FREDUtil.getSite(feature);
			coord = site.getOrigCoordAsCoord();
			datum = site.getOrigCoordDatum();
		} catch (Exception e) {
			//Site wasn't set
		}
	}
	
	private void initialise(Feature feature, int currentFolderID, User user, DAOFactory factory, ContentProvider content) throws StorageAccessException, InsufficientPrivelegesException {
		this.user = user;
		this.factory = factory;
		this.feature = feature;
		this.provider = content;
		
		FolderUtil folderUtil = new FolderUtil(factory);
		
		//check status for editing
		if (!featureUtil.isAllowedEditFeature(user, feature, folderUtil.getUserFolder(currentFolderID, user)))
			throw new InsufficientPrivelegesException("Insufficient rights to create locality");
		if (feature.getAudit().getFolder() != null)
			workingFolder = folderUtil.getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), user);
		
		isAllowedSubmit = featureUtil.isAllowedSubmitFeature(user, feature, workingFolder);
		System.out.println("*************" + isAllowedSubmit);
	}

	public void copyFrom(int featureID) throws InsufficientPrivelegesException, StorageAccessException {
		Feature copyFeature = featureUtil.getFeature(featureID);
		
		if (!feature.getFeatureType().equals(copyFeature.getFeatureType()))
			throw new IllegalArgumentException("Incompatible Locality Types for copy operation");
		
		getFromDatabase(copyFeature);
		/*
		Sample copySample = (Sample)copyFeature.getSamples().iterator().next();
		getFromDatabase(copySample);*/
	}

	protected void getFromDatabase(Feature fromFeature) throws InsufficientPrivelegesException {
		//set fields
		feature.setFeatureName(fromFeature.getFeatureName());
		feature.setRegistrationArea(fromFeature.getRegistrationArea());
		feature.getAudit().setWorkingComments(fromFeature.getAudit().getWorkingComments());
		try {
			site = FREDUtil.getSite(fromFeature);
		} catch (Exception e) {
			//Site wasn't set
		}
	}

	public Integer getFeatureID() {
		return (feature == null) ? null : feature.getFeatureId();
	}

	public String getFeatureType() {
		return feature.getFeatureType();
	}
	
	public void setRegistrationArea(String value) throws DataInputException {
		if (value == null || value.equals(comboNull))
			feature.setRegistrationArea(null);
		else try {
			feature.setRegistrationArea(featureUtil.getRegistrationArea(Integer.parseInt(value)));
		} catch (StorageAccessException e) {
			throw new DataInputException("Registration Area", "Invalid registration area code given");
		}
		
	}

	public List<IconnedLink> getNavigation() {
		List<IconnedLink> links = new Vector<IconnedLink>(4);
		try {
			String args = ((workingFolder == null) ? "?q" : ("?FoldID=" + workingFolder.getFolderId())) 
				+ ((feature.getFeatureId() == null) ? "" : ("&FeatID=" + feature.getFeatureId()))
				+ "&RecType=" + URLEncoder.encode(feature.getFeatureType(), "ISO-8859-1");
			
			links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
		} catch (UnsupportedEncodingException e) {
			//Aint' gonna happen
		}
		links.add(new IconnedLink("javascript:document.form1.SaveType.value='Save';document.form1.submit();", "images/save.gif", "Save"));
		if (isAllowedSubmit)
			links.add(new IconnedLink("javascript:document.form1.SaveType.value='Submit';document.form1.submit();", "images/submit.gif", "Submit"));
		
		return links;
	}
	
	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
        reinitialise(factory);
        Template template = provider.getContent("locality.de.form");
        prepareTemplate(template, provider);
		try {
			//Set up some basic substitutes
            if (feature.getFeatureId() != null)
                template.addSub("featureId", feature.getFeatureId().toString());
			String featureType = feature.getFeatureType();
			template.addSub("featureType", URLEncoder.encode(featureType, "ISO-8859-1"));
			template.addSub("featureName", feature.getFeatureName());
			if (featureType.equals(FREDConstants.OUTCROP))
				template.addSub("isOutcrop", "yes");
			else if (featureType.equals(FREDConstants.DRILLHOLE)) 
				template.addSub("isDrillhole", "yes");
			else
				template.addSub("isVertSect", "yes");
			
			if (workingFolder != null)
				template.addSub("folderId", workingFolder.getFolderId().toString());
			
			//Recollection
			String recollection = getRecollectionInfo();
			if (recollection != null) {
				template.addSub("Recoll", recollection);
			}
			
			if (feature.getAudit().getWorkingComments() != null)
				template.addSub("workingComments", DBUtils.nvl(getComments()));
			
			//Approved?
			Audit audit = feature.getAudit();
			if (audit.getStatus().equals(FREDConstants.APPROVED)) {
				template.addSub("approved", "yes");
				template.loadUntil(out, "{@approvedInformation}");
	
				if (audit.getCuratorComments() != null) {
					String approveName = "";
					try {
						approveName = FREDUtil.getUserName(audit.getApprovedById().intValue());
					} catch (NullPointerException e) {
					}
					String approveDate = "";
					try {
						approveDate = FREDUtil.formatDateForOutput(audit.getApprovedDate());
					} catch (Exception e) {
					}
					out.print("<tr><td>" + approveName + "</td><td class=\"smalltext\">" + approveDate + "</td><td>Curator approval comments: " + DBUtils.nvl(audit.getCuratorComments()) + "</td></tr>");
					
					Set edits = audit.getAuditEdits();
					if (edits != null && edits.size() > 0) {
						for (Iterator i = edits.iterator(); i.hasNext(); ) {
							AuditEdit ae = (AuditEdit) i.next();
							String editName = "";
							try {
								editName = FREDUtil.getUserName(ae.getEditedById().intValue());
							} catch (NullPointerException e) {
							}
							String editDate = "";
							try {
								editDate = FREDUtil.formatDateForOutput(audit.getApprovedDate());
							} catch (Exception e) {
							}
							out.write("<tr><td>" + editName + "</td><td class=\"smalltext\">" + editDate + "</td><td>" + DBUtils.nvl(ae.getComments()) + "</td></tr>");
						}
					}
					out.write("<tr><td class=\"heading\" colspan=\"2\">Edit Comments</td><td><textarea name=\"EditComm\" rows=\"3\" cols=\"40\">"
							+ DBUtils.nvl(editComments)
							+ "</textarea></td></tr>\n");
					out.write("<tr><td>&nbsp;</td></tr>");
				}
			}
			
			//Registration area combo box
			template.loadUntil(out, "{@regCombo}");
			ComboDescriptor cd = new ComboDescriptor("Registration_Area", "reg_area_ID", "Name");
			cd.name = "RegAreaId";
			if (feature.getRegistrationArea() != null)
				cd.selected = feature.getRegistrationArea().getRegAreaId().toString();
			cd.orderBy = "reg_area_id";
			FREDUtil.makeDropBox(out, cd);
	
			//Metadata listing
			template.loadUntil(out, "{@metadataList}");
	
			Set<FeatureMeta> images = feature.getFeatureMetas();
			if (images != null) {
				for (FeatureMeta meta : images) {
					out.println(FREDUtil.getMetaTitle(meta) + "<br />");
				}
			}
			
			//Site setup
			String eastingLabel = "Easting";
			String northingLabel = "Northing";
			
			if (site == null) {
				template.addSub("isNZMG", "yes");
				template.addSub("mapSheetInvisible", "yes");
			} else {
				
				template.addSub("is" + datum.getName(), "yes");
				if (!datum.isMapSheetSystem())
					template.addSub("mapSheetInvisible", "yes");
				else
					template.addSub("mapSheet", ((Datum.MapSheetCoordinate)coord).getMapSheet());
				
				if (datum instanceof NZGD49 || datum instanceof WGS84) {
					eastingLabel = "Longitude";
					northingLabel = "Latitude";
				}
				
				template.addSub("easting", String.valueOf(coord.getEastWest()));
				template.addSub("northing", String.valueOf(coord.getNorthSouth()));
				
				//Accuracy etc
				template.addSub("accuracy", (site.isNull(SiteRecord.H_ACCURACY_FIELD) ? "" : String.valueOf(site.getAccuracy())));
				template.addSub("localityDesc", DBUtils.nvl(feature.getLocality()));
			}
			template.addSub("northingLabel", northingLabel);
			template.addSub("eastingLabel", eastingLabel);
			
			template.loadUntil(out, "{@datumMethodArray}");
			List<DatumMethod> datums = FREDUtil.getSiteDatumMethods();
			for (DatumMethod method : datums)
				out.println("datumMethod[" + method.getKey() + "] = '" + method.getHorizontalAccuracy() + "';\n");
	
			template.loadUntil(out, "{@methodCombo}");
			for (DatumMethod method : datums) {
				out.println("<option value=\"" + method.getKey() + "\"" + ((site != null && method.getKey().equals(String.valueOf(site.getMethod()))) ? " selected" : "")
						+ ">" + method.getValue() + "</option>");
			}
			
			template.loadUntil(out, "{@countryCombo}");
			
			cd = new ComboDescriptor("MIS.Country", "Country_Code", "Country_Name");
			cd.name = "Country";
			cd.prompt = "-- Choose --";
			cd.orderBy = "Country_Name";
			cd.selected = (site == null) ? "NZ" : site.getCountry();
			FREDUtil.makeDropBox(out, cd);
			
		} catch (NamingException e) {
			//Should never happen!
		}
		template.loadAll(out);
	}

	private String getRecollectionInfo() {
		String comments = feature.getAudit().getWorkingComments();
		if (comments == null)
			return null;
        if (comments.startsWith("*Recoll:")) {
            return comments.substring(8, comments.indexOf("*", 8));
		} else
			return null;
	}
	
	private String getComments() {
		String comments = feature.getAudit().getWorkingComments();
		if (comments == null)
			return null;
		if (comments.startsWith("*Recoll:")) {
			return comments.substring(comments.indexOf("*", 8)+1);
		} else
			return comments;
	}

	protected void makeEndBitHTML(PrintWriter out) throws IOException {
		Template template = provider.getContent("locality.de.end");
		if (isAllowedSubmit)
			template.addSub("isAllowedSubmit", "yes");
		template.loadAll(out);
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		out.write("<tr><td>" + feature.getFeatureId() + "</td>");
        out.write("<td>Locality</td>");
		out.write("<td>" + ((workingFolder == null) ? "" : workingFolder.getFolderId()) + "</td>");
		out.write("<td>" + feature.getAudit().getStatus() + "</td>");
		out.write("<td>" + feature.getFeatureType() + "</td>");
		out.write("<td>" + feature.getFeatureName() + "</td>");
		out.write("<td>" + feature.getRegistrationArea().getRegAreaId() + "</td>");
		out.write("<td></td>"); //recollection - do later
		out.write("<td>" + feature.getAudit().getWorkingComments() + "</td>");
		Datum datum = site.getOrigCoordDatum();
		Datum.Coordinate coord = site.getOrigCoordAsCoord();
		out.write("<td>" + datum.getName() + ":" + ((datum.isMapSheetSystem()) ? (((MapSheetCoordinate)coord).getMapSheet() + "*") : "")
				+ coord.getEastWest() + "*" + coord.getNorthSouth() + "</td>");
		out.write("<td>" + site.getMethod() + "</td>");
		out.write("<td>" + site.getAccuracy() + "</td>");
		out.write("<td>" + site.getDirections() + "</td>");	
	}

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
        reinitialise(factory);
        
        Vector<String[]> error = new Vector<String[]>();
		
		//Feature name
		feature.setFeatureName(request.getParameter("FeatName"));
		
		//Registration area
		String registrationAreaId = request.getParameter("RegAreaId");
		System.out.println(registrationAreaId);
		if (feature.getRegistrationArea() == null || !feature.getRegistrationArea().getRegAreaId().toString().equals(registrationAreaId)) {
			if (registrationAreaId.equals("-"))
				feature.setRegistrationArea(null);
			else try {
				feature.setRegistrationArea(factory.getFeatureDAO().getRegistrationArea(Integer.parseInt(registrationAreaId)));
			} catch (StorageAccessException e) {
				e.printStackTrace();
				//Should never happen
			}
		}
		
		//Recollection and working comments
		String recoll = request.getParameter("Recoll");
		String comments = request.getParameter("WorkComm");
		if (recoll.length() > 0) {
			comments = "*Recoll:" + recoll + "*" + comments;
		}
		feature.getAudit().setWorkingComments(comments);
		
		//Site
		datum = DatumFactory.createDatum(request.getParameter("CoordType"));
		coord = null;
		try {
			if (datum.isMapSheetSystem()) {
				coord = (Datum.Coordinate)datum.preferredCoordinate().getConstructor(new Class[] {String.class, double.class, double.class}).newInstance(new Object[] {request.getParameter("MapSheet"), new Double(request.getParameter("North")), new Double(request.getParameter("East"))});
			} else {
				coord = (Datum.Coordinate)datum.preferredCoordinate().getConstructor(new Class[] {double.class, double.class}).newInstance(new Object[] {new Double(request.getParameter("North")), new Double(request.getParameter("East"))});
			}
		} catch (NumberFormatException e) {
			//No problem, there just isn't a site
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		
		if (coord != null) {
			if (!datum.coordinateAcceptable(coord))
				error.add(new String[] {"Coordinate", "Invalid value"});
		
			if (site == null) 
				site = new SiteRecord();
			
			try {
				site.setOriginal(datum.getDatabaseId(), datum.getStringFor(coord));
			} catch (Exception e) {
				error.add(new String[] {"Coordinate", "Invalid coordinates specified"});
			}
			try {
				site.setMethod(Integer.parseInt(request.getParameter("LocMethodID")));
			} catch (Exception e) {
				site.setNull(SiteRecord.H_METHOD_FIELD);
			}
			if (request.getParameter("Accuracy").length() > 0) try {
				site.setAccuracy(Float.parseFloat(request.getParameter("Accuracy")));
			} catch (Exception e) {
				error.add(new String[] {"Accuracy", "Invalid value"});
				site.setNull(SiteRecord.H_ACCURACY_FIELD);
			}
			site.setDirections(request.getParameter("Loc"));
			site.setCountry(request.getParameter("Country"));
		} else
			site = null;
		
		//Also set the feature locality to "Loc"
		feature.setLocality(request.getParameter("Loc"));
		
		editComments = request.getParameter("EditComm");
		
		if (error.size() > 0)
			throw new DataInputException(error);
	}

    /**
     * @param factory
     */
    private void reinitialise(DAOFactory factory) {
        featureUtil = new FeatureUtil(factory);
        if (feature.getFeatureId() != null) try {
            feature = featureUtil.getFeature(feature.getFeatureId().intValue());
        } catch (Exception e) {
        }
    }
	
	
	public int save() throws SQLException, IOException, StorageAccessException, InsufficientPrivelegesException {
		//Check the site with the site DB
		if (site != null) {
			site.key = null;
			try {
				site = FREDUtil.getSite(site);
			} catch (Exception e) {
				throw new StorageAccessException(e);
			}
			feature.setSiteId(new Integer(site.key));
		}
		
		featureUtil.saveFeature(feature, user, editComments);
		
		return feature.getFeatureId();
	}

	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException, DataInputException {
		if (feature.getAudit().getStatus().equals(FREDConstants.WAITING) || !isAllowedSubmit)
			throw new InsufficientPrivelegesException();
		if (feature.getFeatureType() == null || feature.getSiteId() == null || feature.getRegistrationArea() == null)
			throw new MandatoryFieldsMissingException();

		save();
		
		//change status and set Masterfile
		featureUtil.submitFeature(feature, workingFolder, user);
		
		return feature.getFeatureId().intValue();
	}

	public static void revoke(nz.cri.gns.fred.data.Feature feature, User user, PageState state) throws SQLException, IOException, InsufficientPrivelegesException {
		if (!FREDUtils.isAllowedRevokeLocality(user, feature.getAsString(nz.cri.gns.fred.data.Feature.STATUS), String.valueOf(feature.getFeatureID()), state))
			throw new InsufficientPrivelegesException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, nz.cri.gns.fred.data.Audit.STATUS_WORKING);
		qd.addQueryColumn("submitted_by_id", Types.NUMERIC, null);
		qd.addQueryColumn("submitted_date", Types.DATE, null);
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(nz.cri.gns.fred.data.Feature.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		conn.releaseStatement();
		feature = new nz.cri.gns.fred.data.Feature(feature.getFeatureID(), user, state, true);
		refreshSamples(feature, user, state);
		new Folder(feature.getAsInt(nz.cri.gns.fred.data.Feature.MASTERFILE_ID), user, state, true);
	}

	public void approve(FrNumber frNum, String comments) throws SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
		featureUtil.approveFeature(feature, frNum, comments, user);
	}
	
	public void reject(String comments) throws SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
		featureUtil.rejectLocality(feature, comments, user);
	}

	public void delete() throws IOException, SQLException, InsufficientPrivelegesException, StorageAccessException {
		featureUtil.deleteFeature(feature, user);
	}
	
	private static void refreshSamples(nz.cri.gns.fred.data.Feature feature, User user, PageState state) throws InsufficientPrivelegesException, SQLException, IOException {
		if (feature.getSampleCount() > 0) {
			for (Iterator i = feature.getAsVector(nz.cri.gns.fred.data.Feature.SAMPLES).iterator(); i.hasNext(); ) {
				new nz.cri.gns.fred.data.Sample(((Integer) i.next()).intValue(), user, state, true);
			}
		}
	}

	public int getWorkingFolderID() {
		if (workingFolder != null)
			return workingFolder.getFolderId();
		return -1;
	}

	public boolean usesCalendar() {
		return false;
	}
	
	public void makePostFormHTML(PrintWriter out) throws IOException {
	}

}
