/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.core.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author sitikond
 */
public class SiteRevampServiceClient {
    
    private static final Logger log  = Logger.getLogger(SiteRevampServiceClient.class.getName());
    private static RestTemplate restTemplate = new RestTemplate(); 
    private static HttpHeaders headers = new HttpHeaders();
     private static final String LOCALHOST = "http://localhost:8080/site/api/v1";    
//     private static final String POST_SERVICE_URL = "http://localhost:8080/site/api/v1/site";   
//     private static final String PUT_SERVICE_URL = "http://localhost:8080/site/api/v1/site/";
//     private static final String GET_CLOSETO_SERVICE_URL = "http://localhost:8080/site/api/v1/sites/closeto";
//     private static final String GET_SITE_DETAILS_SERVER_URI = "http://localhost:8080/site/api/v1/sites/";
//     private static final String GET_SITE_QUERY_MAPSHEETS_SERVER_URI = "http://localhost:8080/site/api/v1/sites/";
//     private static final String GET_ISLANDS_SERVER_URI = "http://localhost:8080/site/api/v1/islands";
    
    //using the site service running from the portainer
//    private static final String GET_SITE_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1";   
//    private static final String POST_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1";   
//    private static final String PUT_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1";   
//    private static final String GET_CLOSETO_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1";   
//    private static final String GET_SITE_DETAILS_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1"; 
//    private static final String GET_SITE_QUERY_MAPSHEETS_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1";
//    private static final String GET_ISLANDS_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1";
    
    public static String getSite(int siteId ){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/sites/" + siteId;
        String siteDetails = restTemplate.getForObject(endpoint , String.class);
        return siteDetails;
    }
    
    public static String getSiteDetails(int siteId){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/sites/" + siteId + "/details";
        String siteDetails = restTemplate.getForObject(endpoint, String.class);
        return siteDetails;
    }
    
    public static String validateSite(int epsg, String format, String easting, String northing){    
        String validationInfo = null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        //http://localhost:8080/site/api/v1/util/validCoord?epsg=4326&format=D&easting=-44.87&northing=169.84
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/util/validCoord?epsg=" + epsg + "&format=" + format + "&easting=" + easting +
                                                                                                            "&northing=" + northing;
        try{
            validationInfo = restTemplate.getForObject(endpoint, String.class);
        }catch(RestClientException rce){
            rce.printStackTrace();
        }
        return validationInfo;
    }
    
    public static String insertSite(JsonNode siteDetails){    
        String newSiteInfo = null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(siteDetails.toString(), headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/site";
        try{
            newSiteInfo = restTemplate.postForObject(endpoint, request, String.class);
        }catch(RestClientException rce){
            rce.printStackTrace();
        }
        return newSiteInfo;
    }
    
    public static void updateSite(JsonNode existingSiteDetails, int siteId){
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(existingSiteDetails.toString(), headers);
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/site/" + siteId;
        restTemplate.put(endpoint, request, String.class);
    }
    
    public static String[] closeTo(double easting, double northing, double distance, int epsg ){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST)+ "/sites/closeto/" + "?easting=" + easting + "&northing=" + northing + "&metres=" + distance + "&EPSG=" + epsg;
        String[] siteDetails = restTemplate.getForObject(endpoint, String[].class);
        return siteDetails;
    }
    
    public static void insertSiteUser(JsonNode siteUserDetails){    
        JsonNode newSiteUser = null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(siteUserDetails.toString(), headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endpoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/sites/usage/register";
        try{
            restTemplate.postForObject(endpoint, request, String.class);
        }catch(RestClientException rce){
            rce.printStackTrace();
        }
    }

    public static String getSpatialFilter(String spatialFilter) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endPoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/sites/query/mapsheets?" + spatialFilter ;
        return restTemplate.getForObject(endPoint, String.class);
    }

    public static String getIslands() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String endPoint = Environment.getEnvVar("DEFAULT_SITE_API_URL", LOCALHOST) + "/islands";
        return restTemplate.getForObject(endPoint, String.class);
    }
    
}
