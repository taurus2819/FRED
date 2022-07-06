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
     private static final String GET_SITE_SERVER_URI = "http://localhost:8080/site/api/v1/sites/";    
     private static final String POST_SERVICE_URL = "http://localhost:8080/site/api/v1/site";   
     private static final String PUT_SERVICE_URL = "http://localhost:8080/site/api/v1/site/";
     private static final String GET_CLOSETO_SERVICE_URL = "http://localhost:8080/site/api/v1/sites/closeto";
     private static final String GET_SITE_DETAILS_SERVER_URI = "http://localhost:8080/site/api/v1/sites/";
     private static final String GET_SITE_QUERY_MAPSHEETS_SERVER_URI = "http://localhost:8080/site/api/v1/sites/";
     private static final String GET_ISLANDS_SERVER_URI = "http://localhost:8080/site/api/v1/islands";
    
    //using the site service running from the portainer
//   private static final String GET_SITE_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1/sites/";   
//   private static final String POST_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1/site";   
//   private static final String PUT_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1/site/";   
//   private static final String GET_CLOSETO_SERVICE_URL = "http://dev-app.gns.cri.nz:9010/site/api/v1/site/";   
//   private static final String GET_SITE_DETAILS_SERVER_URI = "http://dev-app.gns.cri.nz:9010/site/api/v1/sites/"; 
    
    public static String getSite(int siteId ){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String siteDetails = restTemplate.getForObject(GET_SITE_SERVER_URI + siteId , String.class);
        return siteDetails;
    }
    
    public static String getSiteDetails(int siteId){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        String siteDetails = restTemplate.getForObject(GET_SITE_DETAILS_SERVER_URI + siteId + "/details", String.class);
        return siteDetails;
    }
    
    public static String insertSite(JsonNode siteDetails){    
        String newSiteInfo = null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(siteDetails.toString(), headers);
        // TODO: need proper error handling exceptions if httpResponse
        try{
            newSiteInfo = restTemplate.postForObject(POST_SERVICE_URL, request, String.class);
        }catch(RestClientException rce){
            rce.printStackTrace();
        }
        return newSiteInfo;
    }
    
    public static void updateSite(JSONObject existingSiteDetails, int siteId){
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(existingSiteDetails.toString(), headers);
        restTemplate.put(PUT_SERVICE_URL + siteId, request, String.class);
    }
    
    public static String[] closeTo(double easting, double northing, double distance, int epsg ){        
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        String[] siteDetails = restTemplate.getForObject(GET_CLOSETO_SERVICE_URL + "?easting=" + easting + "&northing=" + northing + "&metres=" + distance + "&EPSG=" + epsg, String[].class);
        System.out.println("SiteRevampServiceClient - closeTo operation" + siteDetails);
        return siteDetails;
    }

    public static String getSpatialFilter(String spatialFilter) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        return restTemplate.getForObject(GET_SITE_QUERY_MAPSHEETS_SERVER_URI + "/query/mapsheets?" + spatialFilter, String.class);
    }

    public static String getIslands() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        // TODO: need proper error handling exceptions if httpResponse
        System.out.println("");
        return restTemplate.getForObject(GET_ISLANDS_SERVER_URI, String.class);
    }
    
    class SiteDataType{
        private String key;
        private Integer intValue;
        private String strValue;
        private Double dblValue;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getIntValue() {
            return intValue;
        }

        public void setIntValue(Integer intValue) {
            this.intValue = intValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

        public Double getDblValue() {
            return dblValue;
        }

        public void setDblValue(Double dblValue) {
            this.dblValue = dblValue;
        }
        
        
    }
    
}
