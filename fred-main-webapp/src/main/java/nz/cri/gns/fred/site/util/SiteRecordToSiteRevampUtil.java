/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import java.io.IOException;
import java.util.logging.Logger;
import nz.cri.gns.fred.site.util.OrigCoordInfoUtil.OrigCoord;
import org.json.JSONObject;

/**
 *
 * @author sitikond
 */
public class SiteRecordToSiteRevampUtil {
    
    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.site.util.SiteRecordToSiteRevampUtil");
    
    public static JSONObject convertSiteRecordToSiteRevamp(JSONObject siteRecordJson) throws IOException{
     /*   "attributes" : {
     *           "SITE_ID" : 2899,
     *           "SITE_NAME" : "98MSR176",										
     *           "LATITUDE"	: -47.1381841205106,
     *           "LONGITUDE" : 167.71135125039,
     *           "METHOD_ID" : null,
     *           "ACCURACY" : null,
     *           "DIRECTIONS" : "middle reaches of Pegasus Creek, upstream of McArthurs Creek",
     *           "ORIG_SYSTEM_ID" : 16,
     *           "ORIG_COORD" : "D9|0860|2830",
     *           "HEIGHT" : null,
     *           "H_METHOD_ID" : null,
     *           "H_ACCURACY" : null,
     *           "COUNTRY_CODE" : "NZ",
     *           "OWNER_ID" : null
     *      }
        */
     
     //Pass on ORIG_SYSTEM_ID and ORIG_COORD and get the epsg,format,gridref,latitude,longitude, easting, northing to be passed as parameters for the 
     //POST or PUT operations for the Site api
        OrigCoord ocDetails = OrigCoordInfoUtil.getJson(siteRecordJson.getInt("ORIG_SYSTEM_ID"), siteRecordJson.getString("ORIG_COORD"));
        JSONObject dataForSiteRevamp = new JSONObject().put("siteName", siteRecordJson.get("SITE_NAME"))
//                                                        .put("methodId", siteRecordJson.get("METHOD_ID") == null ? JSONObject.NULL : siteRecordJson.get("METHOD_ID"))
//                                                        .put("accuracy", siteRecordJson.get("ACCURACY")== null ? JSONObject.NULL : siteRecordJson.get("ACCURACY"))
                                                        .put("directions", siteRecordJson.get("DIRECTIONS")== null ? JSONObject.NULL : siteRecordJson.get("DIRECTIONS"))
//                                                        .put("height", siteRecordJson.get("HEIGHT")== null ? JSONObject.NULL : siteRecordJson.get("HEIGHT"))
//                                                        .put("heightMethodId", siteRecordJson.get("H_METHOD_ID")== null ? JSONObject.NULL : siteRecordJson.get("H_METHOD_ID"))
//                                                        .put("heightAccuracy", siteRecordJson.get("H_ACCURACY")== null ? JSONObject.NULL : siteRecordJson.get("H_ACCURACY"))
                                                        .put("countryCode", siteRecordJson.get("COUNTRY_CODE")== null ? JSONObject.NULL : siteRecordJson.get("COUNTRY_CODE"))
                                                        //.put("comment", siteRecordJson.get(""))
                                                        .put("ownerId", siteRecordJson.get("OWNER_ID")== null ? JSONObject.NULL : siteRecordJson.get("METHOD_ID"))
                                                        .put("epsg", ocDetails.getEpsg())
                                                        .put("gridref", ocDetails.getGridref())
                                                        .put("easting", ocDetails.getEasting())
                                                        .put("northing", ocDetails.getNorthing())
                                                        .put("latitude", ocDetails.getLatitude())
                                                        .put("longitude", ocDetails.getLongitude())
                                                        .put("format", ocDetails.getFormat())
                                                        //.put("auditMsg", siteRecordJson.get(""))
                                                        ;
        return dataForSiteRevamp;
    }
    
    public static JSONObject convertSiteRecordToSiteRevampForUpdate(JSONObject siteRecordJson) throws IOException{
     /*   "attributes" : {
     *           "SITE_ID" : 2899,
     *           "SITE_NAME" : "98MSR176",										
     *           "LATITUDE"	: -47.1381841205106,
     *           "LONGITUDE" : 167.71135125039,
     *           "METHOD_ID" : null,
     *           "ACCURACY" : null,
     *           "DIRECTIONS" : "middle reaches of Pegasus Creek, upstream of McArthurs Creek",
     *           "ORIG_SYSTEM_ID" : 16,
     *           "ORIG_COORD" : "D9|0860|2830",
     *           "HEIGHT" : null,
     *           "H_METHOD_ID" : null,
     *           "H_ACCURACY" : null,
     *           "COUNTRY_CODE" : "NZ",
     *           "OWNER_ID" : null
     *      }
        */
     
     //Pass on ORIG_SYSTEM_ID and ORIG_COORD and get the epsg,format,gridref,latitude,longitude, easting, northing to be passed as parameters for the 
     //POST or PUT operations for the Site api
        OrigCoord ocDetails = OrigCoordInfoUtil.getJson(siteRecordJson.getInt("ORIG_SYSTEM_ID"), siteRecordJson.getString("ORIG_COORD"));
        JSONObject dataForSiteRevamp = new JSONObject().put("site-id", siteRecordJson.get("SITE_ID"))
                                                        .put("siteName", siteRecordJson.get("SITE_NAME"))
                                                        .put("methodId", siteRecordJson.get("METHOD_ID"))
                                                        .put("accuracy", siteRecordJson.get("ACCURACY"))
                                                        .put("directions", siteRecordJson.get("DIRECTIONS"))
                                                        .put("height", siteRecordJson.get("HEIGHT"))
                                                        .put("heightMethodId", siteRecordJson.get("H_METHOD_ID"))
                                                        .put("heightAccuracy", siteRecordJson.get("H_ACCURACY"))
                                                        .put("countryCode", siteRecordJson.get("COUNTRY_CODE"))
                                                        //.put("comment", siteRecordJson.get(""))
                                                        .put("ownerId", siteRecordJson.get("OWNER_ID"))
                                                        .put("epsg", ocDetails.getEpsg())
                                                        .put("gridref", ocDetails.getGridref())
                                                        .put("easting", ocDetails.getEasting())
                                                        .put("northing", ocDetails.getNorthing())
                                                        .put("latitude", ocDetails.getLatitude())
                                                        .put("longitude", ocDetails.getLongitude())
                                                        .put("format", ocDetails.getFormat())
                                                        //.put("auditMsg", siteRecordJson.get(""))
                                                        ;
        return dataForSiteRevamp;
    }
    
}
