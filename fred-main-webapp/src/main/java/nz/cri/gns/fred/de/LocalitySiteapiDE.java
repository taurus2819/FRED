package nz.cri.gns.fred.de;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.DatumMethod;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.site.util.OrigCoordInfoUtil;
import nz.cri.gns.fred.site.util.SiteModel;
import nz.cri.gns.fred.site.util.SiteModelInput;
import nz.cri.gns.fred.site.util.SiteRevampServiceClient;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.SiteModelUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.fred.website.WebsiteConstants;
import nz.cri.gns.html.Attributes;
import nz.cri.gns.html.select.SelectBox;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.util.map.ChathamIslandDatum;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.NZGD2000;
import nz.cri.gns.util.map.NZGD49;
import nz.cri.gns.util.map.WGS84;
import nz.cri.gns.util.map.Datum.MapSheetCoordinate;
import org.xml.sax.SAXException;
import nz.cri.gns.xss.SanitizeHttpServletRequest;
import org.json.JSONObject;


public abstract class LocalitySiteapiDE extends DETemplate implements DataEntryForm {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.de.LocalitySiteDE");

    public static final String comboNull = "-";
    protected DAOFactory factory;
    protected FeatureUtil featureUtil;
    protected ContentProvider provider;
    protected User user;
    private UserFolder workingFolder;
    protected Feature feature;
    private Feature copyFeature;
    protected SanitizeHttpServletRequest sanitizeHttpRequest;
    /**
     * Temporary storage for working comments
     */
    protected String editComments;
    private SiteModel site;
    /**
     * This allows for bad coordinates to still be re-editted
     */
    private Datum.Coordinate coord;
    private Datum datum;
    private boolean isAllowedSave = false;
    private boolean isAllowedSubmit = false;
    private String origCoord;
    private int originSystemId;
    private long ownerId;

    public LocalitySiteapiDE(User user, int folderID, String featureType, DAOFactory factory, ContentProvider content) throws StorageAccessException, InsufficientPrivelegesException {
        featureUtil = new FeatureUtil(factory);
        Feature f = featureUtil.createFeature(folderID, featureType, user);
        initialise(f, folderID, user, factory, content);
        ownerId = user.getId();
    }

    public LocalitySiteapiDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider content) throws InsufficientPrivelegesException, StorageAccessException, IOException {
        featureUtil = new FeatureUtil(factory);
        initialise(feature, folderID, user, factory, content);
        site = SiteModelUtil.getSite(feature);
        coord = SiteModelUtil.getFREDCoordinate(feature);
        datum = SiteModelUtil.getFREDDatum(feature);
        ownerId = user.getId();
    }

    private String getLocalityFromRequest(HttpServletRequest request, ArrayList<String[]> error) {
        //Also set the FRED locality - but first reject & and "
        String loc = request.getParameter("Loc");
        loc = sanitizeHttpRequest.stripAllScripts(loc);
        if (FREDUtil.isEmpty(loc)) {
            error.add(new String[]{"Locality Description", "Empty locality description"});
        }
        return loc;
    }

    private void initialise(Feature feature, int currentFolderID, User user, DAOFactory factory, ContentProvider content) throws StorageAccessException, InsufficientPrivelegesException {
        this.user = user;
        this.factory = factory;
        this.feature = feature;
        this.provider = content;

        FolderUtil folderUtil = new FolderUtil(factory);
        sanitizeHttpRequest = new SanitizeHttpServletRequest();

        //check status
        if (!featureUtil.isAllowedReadFeature(user, feature)) {
            throw new InsufficientPrivelegesException("Insufficient rights to view this locality");
        }
        if (feature.getAudit().getFolder() != null) {
            workingFolder = folderUtil.getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), user);
        }

        isAllowedSave = featureUtil.isAllowedEditFeature(user, feature, workingFolder);
        isAllowedSubmit = featureUtil.isAllowedSubmitFeature(user, feature, workingFolder);
    }

    public void copyFrom(int featureID) throws InsufficientPrivelegesException, StorageAccessException {
        Feature fromFeature = featureUtil.getFeature(featureID);

        if (!feature.getFeatureType().equals(fromFeature.getFeatureType())) {
            throw new IllegalArgumentException("Incompatible Locality Types for copy operation");
        }

        this.copyFeature = fromFeature;
    }

    protected void getFromDatabase(Feature fromFeature) throws InsufficientPrivelegesException, IOException {
        //set fields
        feature.setRegistrationArea(fromFeature.getRegistrationArea());
        feature.getAudit().setWorkingComments(fromFeature.getAudit().getWorkingComments());
        feature.setLocality(fromFeature.getLocality());
        feature.getSiteId();
        site = SiteModelUtil.getSite(fromFeature);      //TODO: get the site from Site api by passing the siteId obtained from FRED feature
        coord = SiteModelUtil.getFREDCoordinate(fromFeature);
        datum = SiteModelUtil.getFREDDatum(fromFeature);
        feature.setMapYear(fromFeature.getMapYear());
        feature.setComments(fromFeature.getComments());
        feature.setCoordComments(fromFeature.getCoordComments());
    }

    public Integer getFeatureID() {
        return (feature == null) ? null : feature.getFeatureId();
    }

    public String getFeatureType() {
        return feature.getFeatureType();
    }

    public void setRegistrationArea(String value) throws DataInputException {
        if (value == null || value.equals(comboNull)) {
            feature.setRegistrationArea(null);
        } else {
            try {
                feature.setRegistrationArea(featureUtil.getRegistrationArea(Integer.parseInt(value)));
            } catch (StorageAccessException e) {
                throw new DataInputException("Registration Area", "Invalid registration area code given");
            }
        }
    }

    @Override
    public List<IconnedLink> getNavigation() {
        List<IconnedLink> links = new ArrayList<>(4);
        try {
            String args = ((workingFolder == null) ? "?q" : ("?FoldID=" + workingFolder.getFolderId()))
                    + ((feature.getFeatureId() == null) ? "" : ("&FeatID=" + feature.getFeatureId()))
                    + "&RecType=" + URLEncoder.encode(feature.getFeatureType(), "ISO-8859-1");
            links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
        } catch (UnsupportedEncodingException e) {
            //Aint' gonna happen
        }
        links.add(new IconnedLink("javascript:submitForm('Save');", "images/save.gif", "Save"));
        if (feature.getFeatureId() == null) {
            isAllowedSubmit = false;
        }
        if (isAllowedSubmit) {
            links.add(new IconnedLink("javascript:submitForm('Submit');", "images/submit.gif", "Submit"));
        }

        return links;
    }

    @Override
    public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException, StorageAccessException {
        reinitialise(factory);
        Template template = provider.getContent("locality.de.form");
        prepareTemplate(template, provider);
        try {
            //Set up some basic substitutes
            if (FeatureUtil.isBacklogFeature(feature)) {
                if (feature.getFrNumber() == null) {
                    template.addSub("noFrNumber", "yes");
                } else {
                    template.addSub("hasFrNumber", "yes");
                    template.addSub("frNumber", (feature.getFrNumber().getFrNumber()));
                }
                template.addSub("needYardFrNumber", "yes");
                template.addSub("yardFrNumber", (feature.getYardFrNumber() != null ? feature.getYardFrNumber().getFrNumber() : ""));
            }
            if (feature.getFeatureId() != null) {
                template.addSub("featureId", feature.getFeatureId().toString());
                template.addSub("hasFeatureId", "yes");
            } else {
                template.addSub("hasFeatureId", "no");
            }
            String featureType = feature.getFeatureType();
            template.addSub("featureType", URLEncoder.encode(featureType, "ISO-8859-1"));
            template.addSub("featureName", feature.getFeatureName());
            if (featureType.equals(FREDConstants.OUTCROP)) {
                template.addSub("isOutcrop", "yes");
            } else if (featureType.equals(FREDConstants.DRILLHOLE)) {
                template.addSub("isDrillhole", "yes");
            } else {
                template.addSub("isVertSect", "yes");
            }

            if (workingFolder != null) {
                template.addSub("folderId", workingFolder.getFolderId().toString());
            }

            String[] comms = FeatureUtil.splitWorkingComments(feature.getAudit().getWorkingComments());
            //Recollection
            String recollection = comms[1];
            if (recollection != null) {
                template.addSub("Recoll", recollection);
            }
            String workComm = comms[0];
            if (workComm != null) {
                template.addSub("workingComments", workComm);
            }

            //Approved/Rejected
            Audit audit = feature.getAudit();
            if (audit.getStatus().equals(FREDConstants.APPROVED)) {
                template.addSub("approved", "yes");
                template.loadUntil(out, "{@approvedInformation}");
                if (audit.getCuratorComments() != null) {
                    out.print("<tr><td>" + ((audit.getApprovedById() != null) ? audit.getApprovedBy().getFullName() : "") + "</td>"
                            + "<td class=\"smalltext\">" + ((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "") + "</td>"
                            + "<td>Curator approval comments: " + DBUtils.nvl(audit.getCuratorComments()) + "</td></tr>");
                }
                for (AuditEdit ae : AuditUtil.getOrderedAuditEdits(audit)) {
                    out.write("<tr><td>" + ((ae.getEditedById() != null) ? ae.getEditedBy().getFullName() : "") + "</td>"
                            + "<td class=\"smalltext\">" + ((ae.getEditedDate() != null) ? FREDUtil.formatDateForOutput(ae.getEditedDate()) : "") + "</td>"
                            + "<td>" + DBUtils.nvl(ae.getComments()) + "</td></tr>");
                }
                out.println("<tr><td class=\"heading\" colspan=\"2\">Edit Comments</td><td><textarea name=\"EditComm\" rows=\"3\" cols=\"40\">"
                        + DBUtils.nvl(editComments)
                        + "</textarea></td></tr>\n");
                out.println("<tr><td>&nbsp;</td></tr>");
            } else if (audit.getStatus().equals(FREDConstants.REJECTED)) {
                template.addSub("isRejected", "yes");
                if (audit.getCuratorComments() != null) {
                    template.addSub("rejComm", audit.getCuratorComments());
                }
            }

            //Registration area combo box
            template.loadUntil(out, "{@regCombo}");
            SelectBox<RegistrationArea> raSelectBox = new SelectBox<RegistrationArea>(featureUtil.getRegistrationAreas());
            Attributes attributes = Attributes.createNameOnlyAttributes("RegAreaId");
            raSelectBox.writeBox(attributes, "-- Choose --", null, (feature.getRegistrationArea() != null) ? feature.getRegistrationArea() : new SiteModelUtil(factory).getRegistrationArea(SiteModelUtil.REG_MAINLAND_NZ), out);

            //Metadata listing
            //template.loadUntil(out, "{@metadataList}");
            //Site setup
            String eastingLabel = "Easting";
            String northingLabel = "Northing";

            if (site == null || coord == null || datum == null) {
                template.addSub("isNZMG", "yes");
                template.addSub("mapSheetInvisible", "yes");
            } else {

                template.addSub("is" + datum.getName(), "yes");
                if (!datum.isMapSheetSystem()) {
                    template.addSub("mapSheetInvisible", "yes");
                } else {
                    template.addSub("mapSheet", ((Datum.MapSheetCoordinate) coord).getMapSheet());
                }

                if (datum instanceof NZGD49 || datum instanceof WGS84 || datum instanceof NZGD2000 || datum instanceof ChathamIslandDatum) {
                    eastingLabel = "Longitude";
                    northingLabel = "Latitude";
                }

                template.addSub("easting", coord.getEastWestString());
                template.addSub("northing", coord.getNorthSouthString());

                //Accuracy etc
                template.addSub("mapYear", DBUtils.nvl(feature.getMapYear()));
                template.addSub("accuracy", (site.getAccuracy() == null) ? "" : String.valueOf(site.getAccuracy()));
                template.addSub("localityDesc", DBUtils.nvl(feature.getLocality()));
            }
            template.addSub("northingLabel", northingLabel);
            template.addSub("eastingLabel", eastingLabel);

            SiteModelUtil siteModelUtil = new SiteModelUtil(factory);

            template.loadUntil(out, "{@datumMethodArray}");
            List<DatumMethod> methods = siteModelUtil.getSiteDatumMethods();
            for (DatumMethod method : methods) {
                out.println("datumMethod[" + method.getMethodId() + "] = '" + method.getNomAccuracyXY() + "';\n");
            }

            template.loadUntil(out, "{@methodCombo}");
            SelectBox<DatumMethod> dSelectBox = new SelectBox<DatumMethod>(methods);
            attributes = Attributes.createNameOnlyAttributes("LocMethodID");
            attributes.setAttribute("onChange", "setAccuracy(this.value, this.form)");
            if(site != null && site.getMethodId() != null){
                dSelectBox.writeBox(attributes, "-- Choose --", null, siteModelUtil.getSiteDatumMethod(site.getMethodId()), out);
            }else if(site != null && site.getMethodId() == null){
                dSelectBox.writeBox(attributes, "-- Choose --", null, siteModelUtil.getSiteDatumMethod(0), out);
            }else if(site == null){
                dSelectBox.writeBox(attributes, "-- Choose --", null, siteModelUtil.getSiteDatumMethod(0), out);
            }

            template.loadUntil(out, "{@countryCombo}");
            SelectBox<Country> cSelectBox = new SelectBox<Country>(featureUtil.getCountries());
            attributes = Attributes.createNameOnlyAttributes("Country");
            cSelectBox.writeBox(attributes, "-- Choose --", null, featureUtil.getCountry((site == null) ? "NZ" : site.getCountryCode()), out);

            template.addSub("coordComm", DBUtils.nvl(feature.getCoordComments()));
            template.addSub("locComm", DBUtils.nvl(feature.getComments()));

        } catch (StorageAccessException e) {
            log.log(Level.SEVERE, null, e);
            throw new IOException("Could not access storage: " + e.getMessage());
        }
        template.loadAll(out);
    }

    protected void makeEndBitHTML(PrintWriter out) throws IOException {
        Template template = provider.getContent("locality.de.end");
        if (isAllowedSubmit) {
            template.addSub("isAllowedSubmit", "yes");
        }
        template.loadAll(out);
    }

    public void makeExcelImportHTML(Writer out) throws IOException, SQLException {
        out.write("<tr><td>" + feature.getFeatureId() + "</td>\n");
        out.write("<td>Locality</td>\n");
        out.write("<td>" + ((workingFolder != null) ? workingFolder.getFolderId() : "") + "</td>\n");
        out.write("<td>" + feature.getAudit().getStatus() + "</td>\n");
        out.write("<td>" + DBUtils.nvl(feature.getAudit().getCuratorComments()) + "</td>\n");
        out.write("<td>" + ((feature.getFrNumber() != null) ? feature.getFrNumber().getFrNumber() : "") + "</td>\n");
        out.write("<td>" + ((feature.getYardFrNumber() != null) ? feature.getYardFrNumber().getFrNumber() : "") + "</td>\n");
        out.write("<td>" + feature.getFeatureType() + "</td>\n");
        out.write("<td>" + DBUtils.nvl(feature.getFeatureName()) + "</td>\n");
        out.write("<td>" + ((feature.getRegistrationArea() != null) ? feature.getRegistrationArea().getRegAreaId() : "") + "</td>\n");
        String[] comms = FeatureUtil.splitWorkingComments(feature.getAudit().getWorkingComments());
        out.write("<td>" + DBUtils.nvl(comms[1]) + "</td>\n");
        out.write("<td>" + DBUtils.nvl(comms[0]) + "</td>\n");
        out.write("<td>" + ((datum != null) ? datum.getName() : "") + "</td>\n");
        out.write("<td>" + ((datum != null && datum.isMapSheetSystem()) ? (((MapSheetCoordinate) coord).getMapSheet()) : "") + "</td>\n");
        out.write("<td>#" + ((coord != null) ? coord.getEastWestString() : "") + "#</td>\n");
        out.write("<td>#" + ((coord != null) ? coord.getNorthSouthString() : "") + "#</td>\n");
        out.write("<td>" + DBUtils.nvl(feature.getMapYear()) + "</td>\n");
        out.write("<td>" + ((site != null && site.getMethodId() > -1) ? String.valueOf(site.getMethodId()) : "") + "</td>\n");
        out.write("<td>" + ((site != null && site.getAccuracy() > -1) ? String.valueOf(site.getAccuracy()) : "") + "</td>\n");
        out.write("<td>" + DBUtils.nvl(feature.getLocality()) + "</td>\n");
        out.write("<td>" + ((site != null && site.getCountryCode() != null) ? site.getCountryCode() : "") + "</td>\n");
    }

    @Override
    public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        reinitialise(factory);
        ArrayList<String[]> error = new ArrayList<>();

//        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()){
//            for (String value : entry.getValue()){
//                System.out.println("Get Parameters = " + value);
//            }
//        }

        //FRNum (if backlog - but only update if null)
        if (FeatureUtil.isBacklogFeature(feature)) {
            try {
                FrNumber frNumber = feature.getFrNumber();
                if (frNumber == null) {
                    String frNumberStr = sanitizeHttpRequest.stripAllScripts(request.getParameter("FRNumber"));
                    //if only map sheet entered then get next available FRNumber
                    if (!frNumberStr.contains("/f")) {
                        frNumber = featureUtil.getNextAvailableFrNumber(frNumberStr);
                    } else {
                        frNumber = featureUtil.getMetricFrNumberByString(frNumberStr, true);
                    }
                    if (featureUtil.getFrNumber(frNumber.getMapSheet(), frNumber.getSerialNumber(), frNumber.getRecollectionNumber()) != null) {
                        error.add(new String[]{"FR Number", "FR Number already defined in database"});
                    } else {
                        feature.setFrNumber(frNumber);
                    }
                }
            } catch (DataInputException e) {
                error.add(new String[]{"FR Number", e.getMessage()});
            } catch (StorageAccessException e) {
                log.log(Level.SEVERE, null, e);
                //Should never happen
            }
            if (!FREDUtil.isEmpty(sanitizeHttpRequest.stripAllScripts(request.getParameter("YardFRNumber")))) {
                try {
                    feature.setYardFrNumber(featureUtil.getYardFrNumberByString(sanitizeHttpRequest.stripAllScripts(request.getParameter("YardFRNumber")), true));
                } catch (DataInputException | StorageAccessException e) {
                    error.add(new String[]{"Yard FR Number", e.getMessage()});
                }
            } else {
                feature.setYardFrNumber(null);
            }
        }

        //Feature name
        feature.setFeatureName(sanitizeHttpRequest.stripAllScripts(request.getParameter("FeatName")));
        
        originSystemId = getOriginSystemId(request.getParameter("CoordType"));
        //easting : longitude;  northing: latitude
        switch(this.originSystemId){
            case 29: //lat|lng
            case 30:
            case 28:
            case 73:
                this.origCoord = request.getParameter("North") + "|" + request.getParameter("East");
                break;
            case 33: //easting|northing
            case 38: //easting|northing
            case 7: //easting|northing
            case 67: //easting|northing
            case 68: //easting|northing
            case 70: //easting|northing
            case 71: //easting|northing
            case 74: //easting|northing
                this.origCoord = request.getParameter("East") + "|" + request.getParameter("North");   
                break;            
            case 16:
            case 72:
            case 17:
            case 69:
                this.origCoord = request.getParameter("MapSheet") + "|" + request.getParameter("East") + "|" + request.getParameter("North");
                break;                
                
        }

        //Registration area
        String registrationAreaId = sanitizeHttpRequest.stripAllScripts(request.getParameter("RegAreaId"));
        if (feature.getRegistrationArea() == null || !feature.getRegistrationArea().getRegAreaId().toString().equals(registrationAreaId)) {
            if (registrationAreaId.equals("-")) {
                feature.setRegistrationArea(null);
            } else {
                try {
                    feature.setRegistrationArea(new FeatureUtil(factory).getRegistrationArea(Integer.parseInt(registrationAreaId)));
                } catch (StorageAccessException e) {
                    log.log(Level.SEVERE, null, e);
                    //Should never happen
                }
            }
        }

        //Recollection and working comments
        feature.getAudit().setWorkingComments(FeatureUtil.combineWorkingComments(sanitizeHttpRequest.stripAllScripts(request.getParameter("Recoll")), sanitizeHttpRequest.stripAllScripts(request.getParameter("WorkComm"))));

        //locality
        String locality = getLocalityFromRequest(request, error);
        // always use FRED user contributed locality and site details
        feature.setLocality(locality);

        // Site
        Double accuracy = null;
        if (request.getParameter("Accuracy").length() > 0) {
            accuracy = Double.parseDouble(request.getParameter("Accuracy"));
        }else{
            accuracy = 0.0;                    
        }

        Integer locMethodID = null;
        try {
            locMethodID = Integer.parseInt(request.getParameter("LocMethodID").equals("-")? "0": request.getParameter("LocMethodID"));
        } catch (NumberFormatException x) {
            x.printStackTrace();
        }
        
        String countryCode = request.getParameter("Country");
        String siteName = request.getParameter("FeatName");
        String locDescr = request.getParameter("Loc");
        String locComms = request.getParameter("LocComm");
        
        // TODO: this part of the code needs to be worked out
//        site = SiteModelUtil.findOrMakeSiteInstance(
//                error,
//                feature.getFeatureName(),
//                feature.getOrigSystemId(),
//                feature.getOrigCoord(),
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("CoordType")),
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("East")),
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("North")),
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("Loc")),
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("Country")),
//                locMethodID,
//                accuracy,
//                sanitizeHttpRequest.stripAllScripts(request.getParameter("MapSheet")),
//                user
//        );
        SiteModelInput smi = null;
        if(site == null ){
            try {
                smi = createNewSite(siteName, locDescr, accuracy, locMethodID, countryCode, locComms);
                Logger.getLogger("EPSG, Format, Easting, Northing =  " + smi.getEpsg() + ", Format = " + smi.getFormat() + ", easting = " + smi.getEasting()
                                    + ", northing = " + smi.getNorthing() + ", Latitude = " + smi.getLatitude() + ", Longitude = " + smi.getLongitude());
                ObjectMapper objMapper = new ObjectMapper();
                if(smi.getEpsg() == 4272 || smi.getEpsg() == 4673 || smi.getEpsg() == 4167 || smi.getEpsg() == 4326){
                    JSONObject validationJson = null;                
                    try {
                        validationJson = new JSONObject(SiteModelUtil.validateSite(smi));
                        Logger.getLogger(validationJson.toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(LocalitySiteapiDE.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(validationJson.getString("message").length() > 0){
                            request.setAttribute("errorMessage", validationJson.getString("message"));
                            error.add(new String[]{"Data Input", validationJson.getString("message")});
                            try {
                                throw new DataInputException("Outcrop Locality", validationJson.getString("message"));
                            } catch (DataInputException ex) {
                                Logger.getLogger(LocalitySiteapiDE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }else{
                        site = SiteModelUtil.insertSite(smi);
                    }    
                }else{
                    site = SiteModelUtil.insertSite(smi);
                }
            } catch (IOException  e) {
                    log.log(Level.SEVERE, null, e);
                try {
                    throw new StorageAccessException(e);
                } catch (StorageAccessException ex) {
                        Logger.getLogger(LocalitySiteapiDE.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (null != site && null == feature.getOrigSystemId()) {
                feature.setOrigSystemId(this.originSystemId);
            }
            if (null != site && null == feature.getOrigCoord()) {
                //caused by: java.sql.BatchUpdateException: ORA-12899: value too large for column "FR"."FEATURE"."ORIG_COORD" (actual: 69, maximum: 36)
                feature.setOrigCoord(this.origCoord);  //feature.setOrigCoord(site.getOrigCoord().toString()); trying to store the json value, but getting BatchUpdateException
            }
        } else if(site.getSiteId() > 0){  //updating an existing record(site values)
            try {
            //this means a site is already existing; trying to update the site info
                int ownerId = Math.toIntExact(this.ownerId);
                OrigCoordInfoUtil.OrigCoord epsgInfo = OrigCoordInfoUtil.getJson(this.originSystemId, this.origCoord);
                int epsg = epsgInfo.getEpsg();
                String gridref = epsgInfo.getGridref();
                Double easting  = epsgInfo.getEasting();
                Double northing = epsgInfo.getNorthing();
                String latitude = epsgInfo.getLatitude();
                String longitude = epsgInfo.getLongitude();
                String format = epsgInfo.getFormat();
                String auditMsg = "User: " + this.user.getFullName() + ", Coord: " + this.origCoord;
                smi = updateSite(siteName, locDescr, accuracy, locMethodID, countryCode, locComms);
                SiteModelUtil.updateSite(site.getSiteId(),smi);
            } catch (IOException ex) {
                Logger.getLogger(LocalitySiteapiDE.class.getName()).log(Level.SEVERE, null, ex);
            }
            feature.setOrigSystemId(this.originSystemId);
            feature.setOrigCoord(this.origCoord); 
        }

            

        //set Map Year
        try {
            if (sanitizeHttpRequest.stripAllScripts(request.getParameter("MapYear")) != null && !sanitizeHttpRequest.stripAllScripts(request.getParameter("MapYear")).equals("")) {
                feature.setMapYear(Integer.parseInt(sanitizeHttpRequest.stripAllScripts(request.getParameter("MapYear"))));
            } else {
                feature.setMapYear(null);
            }
        } catch (NumberFormatException e) {
            error.add(new String[]{"Map Year", "Map Year not numeric"});
        }

            feature.setCoordComments(sanitizeHttpRequest.stripAllScripts(request.getParameter("CoordComm")));
            feature.setComments(sanitizeHttpRequest.stripAllScripts(request.getParameter("LocComm")));

            editComments = sanitizeHttpRequest.sanitizer(request.getParameter("EditComm"));
            editComments = sanitizeHttpRequest.stripAllScripts(editComments);
        
        if (error.size() > 0) {
            throw new DataInputException(error);
        }
    }

    /**
     * @param factory
     */
    private void reinitialise(DAOFactory factory) {
        featureUtil = new FeatureUtil(factory);
        if (feature.getFeatureId() != null) {
            try {
                feature = featureUtil.getFeature(feature.getFeatureId().intValue());
                if (copyFeature != null) {
                    getFromDatabase(copyFeature);
                    copyFeature = null;
                }
            } catch (InsufficientPrivelegesException | StorageAccessException | IOException e) {
            }
        }

    }

    public int save(int dataOriginId) throws SQLException, IOException, StorageAccessException, InsufficientPrivelegesException {
        if (!isAllowedSave) {
            throw new InsufficientPrivelegesException("Insufficient rights to save this locality");
        }
        //Check the site with the site DB        
        if (site != null) {     
            int siteId = site.getSiteId();
            feature.setSiteId(siteId);
            // feature incomplete
            SiteModelUtil siteModelUtil = new SiteModelUtil(factory);
            feature.setSiteView(siteModelUtil.getSiteView(feature.getSiteId()));
        } else {
            feature.setSiteId(null);
        }

        featureUtil.saveFeature(feature, user, editComments, dataOriginId);
        Integer featureId = feature.getFeatureId();        
        return featureId;
    }

    private SiteModelInput createNewSite(String featName, String description, Double accuracy, Integer locMethodId, String countryCode, String comments) throws IOException, JsonProcessingException {
        String siteName = featName;
        int methodID = 0;
        if(locMethodId != null){
            methodID = locMethodId;
        }            
        String directions = description;
        double height = -1;//site.getHeight();
        int heightMethodId = -1; //site.getHeightMethodId();
        double heightAccuracy = -1; //site.getHeightAccuracy();
        String comment = comments;
        int ownerId = Math.toIntExact(this.ownerId);
        OrigCoordInfoUtil.OrigCoord epsgInfo = OrigCoordInfoUtil.getJson(this.originSystemId, this.origCoord);
        int epsg = epsgInfo.getEpsg();
        String gridref = epsgInfo.getGridref();
        Double easting  = epsgInfo.getEasting();
        Double northing = epsgInfo.getNorthing();
        String latitude = epsgInfo.getLatitude();
        String longitude = epsgInfo.getLongitude();
        String format = epsgInfo.getFormat();
        String auditMsg = "User: " + this.user.getFullName() + ", Coord: " + this.origCoord;  

        SiteModelInput smi = new SiteModelInput(siteName, methodID, accuracy, directions, height, heightMethodId, heightAccuracy, countryCode, comment, ownerId,
                epsg, gridref, easting, northing, latitude, longitude, format, auditMsg, "FR.FEATURE.SITE_ID");
        return smi;
    }
    
    private SiteModelInput updateSite(String featName, String description, Double accuracy, Integer locMethodId, String countryCode, String comments) throws IOException, JsonProcessingException {
        String siteName = featName;
        int methodID = 0;
        if(locMethodId != null){
            methodID = locMethodId;
        }            
        String directions = description;
        double height = -1;//site.getHeight();
        int heightMethodId = -1; //site.getHeightMethodId();
        double heightAccuracy = -1; //site.getHeightAccuracy();
        String comment = comments;
        int ownerId = Math.toIntExact(this.ownerId);
        OrigCoordInfoUtil.OrigCoord epsgInfo = OrigCoordInfoUtil.getJson(this.originSystemId, this.origCoord);
        int epsg = epsgInfo.getEpsg();
        String gridref = epsgInfo.getGridref();
        Double easting  = epsgInfo.getEasting();
        Double northing = epsgInfo.getNorthing();
        String latitude = epsgInfo.getLatitude();
        String longitude = epsgInfo.getLongitude();
        String format = epsgInfo.getFormat();
        String auditMsg = "User: " + this.user.getFullName() + ", Coord: " + this.origCoord;  

        SiteModelInput smi = new SiteModelInput(siteName, methodID, accuracy, directions, height, heightMethodId, heightAccuracy, countryCode, comment, ownerId,
                epsg, gridref, easting, northing, latitude, longitude, format, auditMsg, "Updated:FR.FEATURE.SITE_ID");
        return smi;
    }

    public int submit(int dataOriginId) throws InsufficientPrivelegesException, SQLException, IOException, StorageAccessException, DataInputException {
        save(dataOriginId);

        //change status and set Masterfile
        featureUtil.submitFeature(feature, workingFolder, user);
        
        //if the flow has come then there is always a SiteId associated with a feature. if for some unknown reason a SiteId is not associated
        //then its better to check for null
        //call the site usage api
        //a site record is already created when a fred record is imported or created. So, a siteusage is added at at the same time. A separate api call
        //to the SiteUsage is not required. Will keep this code if the requirements change at a later stage.
//        Integer siteId = feature.getSiteId();
//        if(siteId != null){
//            SiteModelUtil.insertSiteUsage(siteId, "FR.FEATURE.SITE_ID");  //using the same format as in SC.SITEUSEAGE table in Oracle DB
//        }

        return feature.getFeatureId().intValue();
    }

    /*
     * Not used??
     private static void refreshSamples(nz.cri.gns.fred.data.Feature feature, User user, PageState state) throws InsufficientPrivelegesException, SQLException, IOException {
     if (feature.getSampleCount() > 0) {
     for (Iterator i = feature.getAsVector(nz.cri.gns.fred.data.Feature.SAMPLES).iterator(); i.hasNext(); ) {
     new nz.cri.gns.fred.data.Sample(((Integer) i.next()).intValue(), user, state, true);
     }
     }
     } */
    public int getWorkingFolderID() {
        if (workingFolder != null) {
            return workingFolder.getFolderId();
        }
        return -1;
    }

    public boolean usesCalendar() {
        return false;
    }

    public void makePostFormHTML(PrintWriter out) throws IOException {
    }

    private int getOriginSystemId(String parameter) {
        int origSysId = 38;
        switch(parameter){
            case "NZMG":
                origSysId = 38;
                break;
            case "NZMS260":
                origSysId = 16;
                break;
            case "NZTM":
                origSysId = 71;
                break;
            case "NZTopo50":
                origSysId = 72;
                break;
            case "NZ Yard SthIsl":
                origSysId = 33;
                break;
            case "NZ Yard NthIsl":
                origSysId = 70;
                break;
            case "NZMS1 SthIsl":
                origSysId = 17;
                break;
            case "NZMS1 NthIsl":
                origSysId = 69;
                break;
            case "Chatham Island Grid":
                origSysId = 7;
                break;
            case "Auckland Island Transverse Mercator":
                origSysId = 67;
                break;
            case "Campbell Island Transverse Mercator":
                origSysId = 68;
                break;
            case "New Caledonia Transverse Mercator":
                origSysId = 74;
                break;
            case "NZGD49":
                origSysId = 29;         //in the table the SYSTEM_CODE column has LL49 - is this NZGD49
                break;
            case "Chatham Island Datum":
                origSysId = 30;
                break;
            case "NZGD2000":
                origSysId = 28;
                break;
            case "WGS84":
                origSysId = 73;
                break;
            default:
                origSysId = 38;    
        }
        return origSysId;
    }
}
