package nz.cri.gns.fred.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.hibernate.Island;
import nz.cri.gns.fred.model.DatumMethod;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.hibernate.SiteView;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.site.util.SiteModel;
import nz.cri.gns.fred.site.util.SiteModelInput;
import nz.cri.gns.fred.site.util.SiteRevampServiceClient;
import static nz.cri.gns.fred.site.util.SiteRevampServiceClient.getSiteDetails;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.Datum.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;

public class SiteModelUtil extends ModelUtil {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.util.SiteModelUtil");

    private FredDAO fredDAO;
    public static final int REG_MAINLAND_NZ = 400;
    public static final int REG_CHATHAM_ISLANDS = 401;
    public static final int REG_ROSS_SEA = 402;
    public static final int REG_NEW_CALEDONIA = 403;
    public static final int REG_TOKELAU = 404;
    public static final int REG_FIJI = 405;
    public static final int REG_SAMOA = 406;
    public static final int REG_NIUE = 407;
    public static final int REG_COOK_ISLANDS = 408;
    public static final int REG_NORFOLK_ISLAND = 409;
    public static final int REG_TONGA = 410;
    public static final int REG_LORD_HOWE_ISLAND = 411;
    public static final int REG_KERMADEC_ISLANDS = 412;
    public static final int REG_BOUNTY_ISLANDS = 413;
    public static final int REG_THE_SNARES = 414;
    public static final int REG_CAMPBELL_ISLAND = 415;
    public static final int REG_AUCKLAND_ISLANDS = 416;
    public static final int REG_ANTIPODES_ISLANDS = 417;
    public static final int REG_MACQUARIE_ISLAND = 418;
    public static final int REG_OTHER = 419;
    public static final int REG_VANUATU = 420;
    public static final int REG_PAPUA_NEW_GUINEA = 421;
    public static final int MASTERFILE_NTH_NI = 1;
    public static final int MASTERFILE_CEN_NI = 2;
    public static final int MASTERFILE_STH_NI = 3;
    public static final int MASTERFILE_NELSON = 4;
    public static final int MASTERFILE_CEN_SI = 5;
    public static final int MASTERFILE_STH_SI = 6;
    public static final int MASTERFILE_NZ_ISLANDS = 7;
    public static final int MASTERFILE_ANTARCTICA = 8;
    public static final int MASTERFILE_PACIFIC_ISLANDS = 9;
    public static final int MASTERFILE_NEW_CALEDONIA = 10;
    public static final int MASTERFILE_OFFSHORE = 11;
    //This is a special backlog masterfile folder
    public static final int MASTERFILE_NTH_NI_BACKLOG = 14;
    public static final int MASTERFILE_CEN_NI_BACKLOG = 17;
    public static final int MASTERFILE_STH_NI_BACKLOG = 19;
    public static final int MASTERFILE_NELSON_BACKLOG = 12;
    public static final int MASTERFILE_CEN_SI_BACKLOG = 20;
    public static final int MASTERFILE_STH_SI_BACKLOG = 22;
    public static final int MASTERFILE_NZ_ISLANDS_BACKLOG = 23;
    public static final int MASTERFILE_ANTARCTICA_BACKLOG = 24;
    public static final int MASTERFILE_PACIFIC_ISLANDS_BACKLOG = 25;
    public static final int MASTERFILE_NEW_CALEDONIA_BACKLOG = 26;
    public static final int MASTERFILE_OFFSHORE_BACKLOG = 27;

    public SiteModelUtil(DAOFactory factory) {
        super(factory);
        this.fredDAO = factory.getFredDAO();
    }

    public static SiteView getSiteView(int siteId) {
        //return fredDAO.get(siteId, nz.cri.gns.fred.hibernate.SiteView.class);
        //ADD loadSiteDetails(int siteId) from the QueryService in PetLab
        SiteView siteView = new SiteView();
        String siteDetailed = getSiteDetails(siteId);
        siteView.setSiteId(siteId);
        
        try {
            JSONObject siteJson = new JSONObject(siteDetailed);
            JSONObject siteModelJson = siteJson.getJSONObject("model");
            
            if(!siteModelJson.isNull("siteName")){
                siteView.setSiteName(siteModelJson.getString("siteName"));
            }
            siteView.setLatitude(siteModelJson.getDouble("lat"));
            siteView.setLongitude(siteModelJson.getDouble("lon"));
            if(!siteModelJson.isNull("directions")){
                siteView.setDirections(siteModelJson.getString("directions")); 
            }
            if(!siteModelJson.isNull("countryCode")){
                siteView.setCountryName(siteModelJson.getString("countryCode"));
            }
            if(!siteModelJson.isNull("methodId"))    {
                siteView.setMethodId(siteModelJson.getInt("methodId"));
            }
            if(!siteModelJson.isNull("accuracy"))    {
                siteView.setHeight(siteModelJson.getDouble("accuracy"));
            }
            if(!siteModelJson.isNull("height"))    {
                siteView.setHeight(siteModelJson.getDouble("height"));
            }
            if(siteJson.has("island") && !siteJson.isNull("island")){
                siteView.setIsland(siteJson.getJSONObject("island").getString("name"));
            }
            if(siteJson.has("nzms262Sheet") && !siteJson.isNull("nzms262Sheet")){
                siteView.setNzms262Sheet(siteJson.getString("nzms262Sheet"));
            }
            if(siteJson.has("nzms260Sheet") && !siteJson.isNull("nzms260Sheet")){
                siteView.setNzms260Sheet(siteJson.getString("nzms260Sheet"));
            }
            if(siteJson.has("topo50Sheet") && !siteJson.isNull("topo50Sheet")){
                siteView.setTopo50Sheet(siteJson.getString("topo50Sheet"));
            }
            if(siteJson.has("qmapsheet") && !siteJson.isNull("qmapsheet")){
                siteView.setQmapSheet(siteJson.getString("qmapsheet"));
            }
        } catch (ParseException ex) {
            Logger.getLogger(SiteModelUtil.class.getName()).log(Level.SEVERE, "Site details unavailable");
        }
        return siteView;
    }
    
    public static int getMainLandMasterfile(double lat, double lon, boolean isBacklog) {

        if (lon <= 176.458899 && lat >= -38.901906) {
            return (isBacklog) ? MASTERFILE_NTH_NI_BACKLOG : MASTERFILE_NTH_NI;
        }
        if (lat >= -39.712028 || (lon >= 175.597906 && lat >= -40.546957)) {
            return (isBacklog) ? MASTERFILE_CEN_NI_BACKLOG : MASTERFILE_CEN_NI;
        }
        if (lon >= 174.611 || lat >= -40.024067) {
            return (isBacklog) ? MASTERFILE_STH_NI_BACKLOG : MASTERFILE_STH_NI;
        }
        if (lat >= -41.926979) {
            return (isBacklog) ? MASTERFILE_NELSON_BACKLOG : MASTERFILE_NELSON;
        }
        if (lon >= 169.222279 && lat >= -44.566144) {
            return (isBacklog) ? MASTERFILE_CEN_SI_BACKLOG : MASTERFILE_CEN_SI;
        }
        if ((lat >= -47.531892)) {
            return (isBacklog) ? MASTERFILE_STH_SI_BACKLOG : MASTERFILE_STH_SI;
        }
        return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
    }

    public static int getMasterfile(Feature feature) throws SQLException, NamingException, IOException {
        boolean isBacklog = FeatureUtil.isBacklogFeature(feature);
        switch (feature.getRegistrationArea().getRegAreaId()) {
            case REG_MAINLAND_NZ:
                SiteModel site = getSite(feature);
                return getMainLandMasterfile(site.getLat(), site.getLon(), isBacklog);

            case REG_CHATHAM_ISLANDS:
            case REG_CAMPBELL_ISLAND:
            case REG_AUCKLAND_ISLANDS:
            case REG_ANTIPODES_ISLANDS:
            case REG_THE_SNARES:
                return (isBacklog) ? MASTERFILE_NZ_ISLANDS_BACKLOG : MASTERFILE_NZ_ISLANDS;
            case REG_ROSS_SEA:
                return (isBacklog) ? MASTERFILE_ANTARCTICA_BACKLOG : MASTERFILE_ANTARCTICA;
            case REG_TOKELAU:
            case REG_FIJI:
            case REG_SAMOA:
            case REG_NIUE:
            case REG_COOK_ISLANDS:
            case REG_NORFOLK_ISLAND:
            case REG_TONGA:
            case REG_LORD_HOWE_ISLAND:
            case REG_KERMADEC_ISLANDS:
            case REG_BOUNTY_ISLANDS:
            case REG_MACQUARIE_ISLAND:
            case REG_VANUATU:
            case REG_PAPUA_NEW_GUINEA:
                return (isBacklog) ? MASTERFILE_PACIFIC_ISLANDS_BACKLOG : MASTERFILE_PACIFIC_ISLANDS;
            case REG_NEW_CALEDONIA:
                return (isBacklog) ? MASTERFILE_NEW_CALEDONIA_BACKLOG : MASTERFILE_NEW_CALEDONIA;
            case REG_OTHER:
                return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
        }
        return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
    }

    public static Datum.LatLong getSiteLatLong(Feature feature) throws SQLException, NamingException, IOException {
        SiteModel sm = getSite(feature);
        return new Datum.LatLong(sm.getLat(), sm.getLon());
    }

    public static String getFrNumberMapSheet(Feature feature) throws IOException {
        //get the NZMS260 coord - use the /details api from the site api. This provides the NZMS260 sheet 
        SiteView sv = getSiteView(feature.getSiteId());   
        String mapsheet = null;
        if(!sv.getNzms260Sheet().equals("invalid")){ //all outside mainland NZ
            mapsheet = sv.getNzms260Sheet();
        }else{
            switch(feature.getRegistrationArea().getRegAreaId()){
                case REG_CHATHAM_ISLANDS:
                    mapsheet = "CH";
                    break;
                case REG_FIJI:
                    mapsheet = "FJ";
                    break;
                case REG_ROSS_SEA:
                    mapsheet = "RS";
                    break;
                case REG_TOKELAU:
                    mapsheet = "TK";
                    break;
                case REG_SAMOA:
                    mapsheet = "WS";
                    break;
                case REG_COOK_ISLANDS:
                    mapsheet = "CK";
                    break;
                case REG_NIUE:
                    mapsheet = "NU";
                    break;
                case REG_TONGA:
                    mapsheet = "TO";
                    break;
                case REG_NORFOLK_ISLAND:
                    mapsheet = "NF";
                    break;
                case REG_VANUATU:
                    mapsheet = "VU";
                    break;
                case REG_PAPUA_NEW_GUINEA:
                    mapsheet = "PG";
                    break;
                case REG_NEW_CALEDONIA:
                    mapsheet = "NC";
                    break;
                case REG_OTHER:   //offshore regions
//                    mapsheet = "IW";
                    SiteModel site = getSite(feature);
                    if(site.getLat() < 0 && site.getLon() > 0){  //lat is -ve and lon is +ve
                        mapsheet = "SE" + Math.abs((int)site.getLat().doubleValue()) + Math.abs((int)site.getLon().doubleValue());
                    }else if(site.getLat() > 0 && site.getLon() > 0){ //lat is +ve and lon is +ve
                        mapsheet = "NE" + Math.abs((int)site.getLat().doubleValue()) + Math.abs((int)site.getLon().doubleValue());
                    }else if(site.getLat() > 0 && site.getLon() < 0){ //lat is +ve and lon is -ve
                        mapsheet = "NW" + Math.abs((int)site.getLat().doubleValue()) + Math.abs((int)site.getLon().doubleValue());
                    }else if(site.getLat() < 0 && site.getLon() < 0){ //lat is -ve and lon is -ve
                        mapsheet = "SW" + Math.abs((int)site.getLat().doubleValue()) + Math.abs((int)site.getLon().doubleValue());
                    }
                    break;                    
            }                    
        }
        return mapsheet;
    }

    public static SiteModel getSite(Feature feature) throws IOException {
        SiteModel sm = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String existingSite = SiteRevampServiceClient.getSite(feature.getSiteId());
            sm = objectMapper.readValue(existingSite, SiteModel.class);
//            sm = nz.cri.gns.db.util.SiteUtil.querySite(feature.getSiteId());
        } catch (JsonProcessingException e) {
                System.out.println(e.getClass().getName() + 
                " : " + e.getOriginalMessage());
        }
        return sm;
    }
    
    /**
     * inserts an appropriate record from the Site API for the given site , inserting if
     * necessary
     *
     * @throws IOException
     * 
     * return a SiteModel
     */
    public static SiteModel insertSite(SiteModelInput smi) throws IOException {
        SiteModel sm = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String inputSiteModel = objectMapper.writeValueAsString(smi);
        try {            
            JsonNode node = objectMapper.readTree(inputSiteModel);
            String newSite = SiteRevampServiceClient.insertSite(node);
            if(newSite != null){
                sm = objectMapper.readValue(newSite, SiteModel.class);
            }
//        return nz.cri.gns.db.util.SiteUtil.insertSite(site);
        } catch (JsonProcessingException e) {
                System.out.println(e.getClass().getName() + 
                " : " + e.getOriginalMessage());
        }
        return sm;
    }

    /**
     * Validates if the lat/long coordinates are valid 
     *
     * @param smi
     * @return 
     * @throws IOException
     * 
     * return a SiteModel
     */
    public static String validateSite(SiteModelInput smi) throws IOException, ParseException {
        String validationMessage = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String inputSiteModel = objectMapper.writeValueAsString(smi);
        JSONObject jsonObj = new JSONObject(inputSiteModel);
        String easting = !jsonObj.get("easting").equals(null) ? jsonObj.getString("easting") : jsonObj.getString("longitude");
        String northing = !jsonObj.get("northing").equals(null) ? jsonObj.getString("northing") : jsonObj.getString("latitude");
        int epsg = jsonObj.getInt("epsg");
        String format = jsonObj.getString("format");
        validationMessage = SiteRevampServiceClient.validateSite(epsg, format, easting, northing);
        return validationMessage;
    }
    
    public static void updateSite(Integer siteId, SiteModelInput smi) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputSiteModel = objectMapper.writeValueAsString(smi);
        try {            
            JsonNode node = objectMapper.readTree(inputSiteModel);
            SiteRevampServiceClient.updateSite(node, siteId);
//        return nz.cri.gns.db.util.SiteUtil.insertSite(site);
        } catch (JsonProcessingException e) {
                System.out.println(e.getClass().getName() + 
                " : " + e.getOriginalMessage());
        }
    }
    
    public static List<Island> getIslands(){
        List<Island> islands = new ArrayList<>();
        
        String response = SiteRevampServiceClient.getIslands();
        try {
            JSONArray siteModelArray = new JSONArray(response);
            JSONObject fullObject;
            for (int val = 0; val < siteModelArray.length(); val++) {
                fullObject = siteModelArray.getJSONObject(val);
                Island island = new Island();
                island.setName(fullObject.getString("name"));
                islands.add(island);
            }
        } catch (ParseException ex) {
            Logger.getLogger(SiteModelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return islands;
    }
    
    /**
     * inserts an appropriate record from the Site API for the given site , inserting if
     * necessary
     *
     * @throws IOException
     * 
     * return a SiteModel
     */
    public static void insertSiteUsage(int siteId, String clientFeature) throws IOException {
        SiteUsage su = new SiteUsage(siteId, clientFeature);
        ObjectMapper objectMapper = new ObjectMapper();
        String inputSiteUsr = objectMapper.writeValueAsString(su);
        try {            
            JsonNode node = objectMapper.readTree(inputSiteUsr);
            SiteRevampServiceClient.insertSiteUser(node);
        } catch (JsonProcessingException e) {
                System.out.println(e.getClass().getName() + 
                " : " + e.getOriginalMessage());
        }
    }
    
    
    public static List<Integer> requestSitesBySpatialFilter(String spatialFilter) {                
        
        spatialFilter = spatialFilter.replaceAll("QMAP_SHEET","qmapSheet");
        spatialFilter = spatialFilter.replaceAll("NZMG_SHEET","nzmgSheet");
        spatialFilter = spatialFilter.replaceAll("COUNTRY_CODE","countryCode");
        spatialFilter = spatialFilter.replaceAll("ISLAND","island");
        spatialFilter = spatialFilter.replaceAll(";","&");
            
        String response = SiteRevampServiceClient.getSpatialFilter(spatialFilter);
            
        List<Integer> siteIdList = new ArrayList<>();
        try {
            JSONArray siteModelArray = new JSONArray(response);
            JSONObject fullObject;
            for (int val = 0; val < siteModelArray.length(); val++) {
                fullObject = siteModelArray.getJSONObject(val);
                siteIdList.add(Integer.parseInt(fullObject.get("siteId").toString()));
            }
        } catch (ParseException ex) {
            Logger.getLogger(SiteModelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return siteIdList;        
    }
    
    /**
     * For performance reasons, only Site IDs are returned from the API for filter queries.
     * @param jsonString
     * @return
     * @throws ParseException
     * @throws IOException 
     */
    private static List<Integer> parseIds(String jsonString) throws ParseException, IOException {
        //System.err.println("Site response: " + jsonString);
        JSONArray siteModelArray = new JSONArray(jsonString);
        List<Integer> siteIdList = new ArrayList<>();
        JSONObject fullObject;
        for (int val = 0; val < siteModelArray.length(); val++) {
            fullObject = siteModelArray.getJSONObject(val);
            siteIdList.add(Integer.parseInt(fullObject.get("siteId").toString()));
        }
        
        return siteIdList;
    }

    public DatumMethod getSiteDatumMethod(int methodId) throws StorageAccessException {
        return fredDAO.get(methodId, nz.cri.gns.fred.hibernate.DatumMethod.class);
    }

    public List<DatumMethod> getSiteDatumMethods() throws StorageAccessException {
        return fredDAO.getList("FROM DatumMethod AS d WHERE d.nomAccuracyXY IS NOT NULL", DatumMethod.class);
    }

    public RegistrationArea getRegistrationArea(int id) throws StorageAccessException {
        return fredDAO.get(id, nz.cri.gns.fred.hibernate.RegistrationArea.class);
    }

    public static Datum getFREDDatum(Feature feature) {
        if (feature.getOrigSystemId() == null) {
            return null;
        }
        Datum datum = DatumFactory.createDatum(feature.getOrigSystemId().intValue());
        return datum;
    }

    public static Coordinate getFREDCoordinate(Feature feature) {
        if (feature.getOrigCoord() == null || feature.getOrigSystemId() == null) {
            return null;
        }
        Datum datum = getFREDDatum(feature);
        Coordinate coord = datum.parseCoordinate(feature.getOrigCoord());
        return coord;
    }
    
    /**
     * Returns true if the string is not null, empty, or only has whitespace.
     * @param s the string
     * @return whether the String has data
     */
    public static boolean hasData(String s){
        return (null != s && !s.isEmpty() && !s.trim().isEmpty());
    }

    private static class SiteUsage {
        private Integer siteId;
        private String usedBy;
        
        public SiteUsage(Integer siteId, String usedBy) {
            this.usedBy = usedBy;
            this.siteId = siteId;
        }

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public String getUsedBy() {
            return usedBy;
        }

        public void setUsedBy(String usedBy) {
            this.usedBy = usedBy;
        }

        
        
    }

}
