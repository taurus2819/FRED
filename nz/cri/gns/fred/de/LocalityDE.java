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
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.site.DatumMethod;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dao.DAOFactory;
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
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.NZGD2000;
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
	private Feature copyFeature;
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
		initialise((featureUtil = new FeatureUtil(factory)).createFeature(folderID, featureType, user), folderID, user, factory, content);
	}

	public LocalityDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider content) throws InsufficientPrivelegesException, StorageAccessException {
        featureUtil = new FeatureUtil(factory);
		initialise(feature, folderID, user, factory, content);
		try {
			site = FREDUtil.getSite(feature);
			coord = FREDUtil.getFREDCoordinate(feature);
			datum = FREDUtil.getFREDDatum(feature);
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
	}

	public void copyFrom(int featureID) throws InsufficientPrivelegesException, StorageAccessException {
		Feature fromFeature = featureUtil.getFeature(featureID);
		
		if (!feature.getFeatureType().equals(fromFeature.getFeatureType()))
			throw new IllegalArgumentException("Incompatible Locality Types for copy operation");
		
		this.copyFeature = fromFeature;
	}

	protected void getFromDatabase(Feature fromFeature) throws InsufficientPrivelegesException {
		//set fields
		feature.setRegistrationArea(fromFeature.getRegistrationArea());
		feature.getAudit().setWorkingComments(fromFeature.getAudit().getWorkingComments());
		feature.setLocality(fromFeature.getLocality());
		try {
			site = FREDUtil.getSite(fromFeature);
			coord = FREDUtil.getFREDCoordinate(fromFeature);
			datum = FREDUtil.getFREDDatum(fromFeature);
		} catch (Exception e) {
			e.printStackTrace();
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
			if (FeatureUtil.isBacklogFeature(feature)) {
				if (feature.getFeatureId() == null) {
					template.addSub("isNewBacklog", "yes");
				} else {
					template.addSub("isExistingBacklog", "yes");
					FrNumber frNumber = feature.getFrNumber();
					template.addSub("frNumber", (frNumber != null ? frNumber.getFrNumber() : ""));
				}
			}
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
			
			String[] comms = FeatureUtil.splitWorkingComments(feature.getAudit().getWorkingComments());
			//Recollection
			String recollection = comms[1];
			if (recollection != null) {
				template.addSub("Recoll", recollection);
			}
			String workComm = comms[0];
			if (workComm != null)
				template.addSub("workingComments", workComm);
			
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
			
			if (site == null || coord == null || datum == null) {
				template.addSub("isNZMG", "yes");
				template.addSub("mapSheetInvisible", "yes");
			} else {
				
				template.addSub("is" + datum.getName(), "yes");
				if (!datum.isMapSheetSystem())
					template.addSub("mapSheetInvisible", "yes");
				else
					template.addSub("mapSheet", ((Datum.MapSheetCoordinate)coord).getMapSheet());
				
				if (datum instanceof NZGD49 || datum instanceof WGS84 || datum instanceof NZGD2000) {
					eastingLabel = "Longitude";
					northingLabel = "Latitude";
				}
				
				template.addSub("easting", coord.getEastWestString());
				template.addSub("northing", coord.getNorthSouthString());
				
				//Accuracy etc
				template.addSub("mapYear", DBUtils.nvl(feature.getMapYear()));
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
			e.printStackTrace();
			//Should never happen!
		}
		template.loadAll(out);
	}

	protected void makeEndBitHTML(PrintWriter out) throws IOException {
		Template template = provider.getContent("locality.de.end");
		if (isAllowedSubmit)
			template.addSub("isAllowedSubmit", "yes");
		template.loadAll(out);
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
		out.write("<tr><td>" + feature.getFeatureId() + "</td>\n");
        out.write("<td>Locality</td>\n");
		out.write("<td>" + ((workingFolder != null) ? workingFolder.getFolderId() : "") + "</td>\n");
		out.write("<td>" + feature.getAudit().getStatus() + "</td>\n");
		out.write("<td>" + DBUtils.nvl(feature.getAudit().getCuratorComments()) + "</td>\n");
		out.write("<td>" + ((feature.getFrNumber() != null) ? feature.getFrNumber().getFrNumber() : "") + "</td>\n");
		out.write("<td>" + feature.getFeatureType() + "</td>\n");
		out.write("<td>" + DBUtils.nvl(feature.getFeatureName()) + "</td>\n");
		out.write("<td>" + feature.getRegistrationArea().getRegAreaId() + "</td>\n");
		out.write("<td></td>\n"); //recollection - do later
		out.write("<td>" + DBUtils.nvl(feature.getAudit().getWorkingComments()) + "</td>\n");
		out.write("<td>" + ((datum != null) ? datum.getName() : "") + "</td>\n");
		out.write("<td>" + ((datum != null && datum.isMapSheetSystem()) ? (((MapSheetCoordinate)coord).getMapSheet()) : "") + "</td>\n");
		out.write("<td>" + ((coord != null) ? coord.getEastWestString() : "") + "</td>\n");
		out.write("<td>" + ((coord != null) ? coord.getNorthSouthString() : "") + "</td>\n");
		out.write("<td>" + DBUtils.nvl(feature.getMapYear()) + "</td>\n");
		out.write("<td>" + ((site != null && !site.isNull(SiteRecord.H_METHOD_FIELD)) ? String.valueOf(site.getMethod()) : "") + "</td>\n");
		out.write("<td>" + ((site != null && !site.isNull(SiteRecord.H_ACCURACY_FIELD)) ? String.valueOf(site.getAccuracy()) : "") + "</td>\n");
		out.write("<td>" + ((site != null && !site.isNull(SiteRecord.DIRECTIONS_FIELD)) ? site.getDirections() : "") + "</td>\n");
		out.write("<td>" + ((site != null && !site.isNull(SiteRecord.COUNTRY_FIELD)) ? site.getCountry() : "") + "</td>\n");
	}

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        reinitialise(factory);
        
        Vector<String[]> error = new Vector<String[]>();
		
        //FRNum (if backlog - but only update if null)
        if (FeatureUtil.isBacklogFeature(feature)) {
        	try {
        		String frNumberStr = request.getParameter("FRNumber");
        	   	FrNumber frNumber = feature.getFrNumber();
        	   	if (frNumber == null) {
        	   		frNumber = FeatureUtil.parseFRNumber(frNumberStr);
        	   		feature.setFrNumber(frNumber);
        	   	}
   			} catch (DataInputException e) {
				error.add(new String[] {"FR Number", e.getMessage()});
			}
        }
        
		//Feature name
		feature.setFeatureName(request.getParameter("FeatName"));
		
		//Registration area
		String registrationAreaId = request.getParameter("RegAreaId");
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
		feature.getAudit().setWorkingComments(FeatureUtil.combineWorkingComments(request.getParameter("Recoll"), request.getParameter("WorkComm")));
		
		//Site
		datum = DatumFactory.createDatum(request.getParameter("CoordType"));
		coord = null;
		String east = request.getParameter("East");
		String north = request.getParameter("North");
		if (east != null && !east.equals("") && north != null && !north.equals("")) {
			try {
				if (datum.isMapSheetSystem()) {
					int precision = east.length();
					if (north.length() != precision) {
						error.add(new String[] {"Coordinate", "Truncated coordinates different lengths"});
					} else if ((precision > 0 && precision < 3) || precision > 4) {
						error.add(new String[] {"Coordinate", "Length of truncated coordinates must be 3 or 4"});
					} else {
						coord = (Datum.Coordinate)datum.preferredCoordinate().getConstructor(new Class[] {double.class, double.class, String.class, int.class}).newInstance(new Object[] {new Double(north), new Double(east), request.getParameter("MapSheet"), precision});
					}
				} else {
					coord = (Datum.Coordinate)datum.preferredCoordinate().getConstructor(new Class[] {double.class, double.class}).newInstance(new Object[] {new Double(north), new Double(east)});
				}
			} catch (NumberFormatException e) {
				error.add(new String[] {"Coordinate", "Non numeric coordinate entered"});
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		
		if (coord != null) {
			if (!datum.coordinateAcceptable(coord))
				error.add(new String[] {"Coordinate", "Invalid value"});
		
			if (site == null) 
				site = new SiteRecord();
			
			try {
				site.setOriginal(datum.getDatabaseId(), datum.getStringFor(coord));
				//Also set the FRED copied SITE fields
				feature.setOrigSystemId(datum.getDatabaseId());
				feature.setOrigCoord(datum.getStringFor(coord));
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
			site.setOwner(user.getPersonId());
		} else {
			site = null;
			feature.setOrigSystemId(null);
			feature.setOrigCoord(null);
		}
		
		
		//Also set the FRED locality
		feature.setLocality(request.getParameter("Loc"));

		//set Map Year
		try {
			if (request.getParameter("MapYear") != null && !request.getParameter("MapYear").equals("")) {
				feature.setMapYear(Integer.parseInt(request.getParameter("MapYear")));
			} else {
				feature.setMapYear(null);
			}
		} catch (Exception e) {
			error.add(new String[] {"Map Year", "Map Year not numeric"});
		}
		
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
            if (copyFeature != null) {
            	getFromDatabase(copyFeature);
            	copyFeature = null;
            }
        } catch (Exception e) {
        }

    }
	
	public int save(int dataOriginId) throws SQLException, IOException, StorageAccessException, InsufficientPrivelegesException {
		//Check the site with the site DB
		if (site != null) {
			site.key = null;
			try {
				site = FREDUtil.getSite(site);
			} catch (Exception e) {
				throw new StorageAccessException(e);
			}
			feature.setSiteId(new Integer(site.key));
		} else {
			feature.setSiteId(null);
		}
		
		featureUtil.saveFeature(feature, user, editComments, dataOriginId);
		
		return feature.getFeatureId();		
	}
	
	public int submit(int dataOriginId) throws SQLException, IOException, InsufficientPrivelegesException, DataInputException, StorageAccessException, DataInputException {
		if (feature.getAudit().getStatus().equals(FREDConstants.WAITING) || !isAllowedSubmit)
			throw new InsufficientPrivelegesException();
		if (feature.getFeatureType() == null || feature.getSiteId() == null || feature.getRegistrationArea() == null)
			throw new MandatoryFieldsMissingException();

		save(dataOriginId);
		
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
