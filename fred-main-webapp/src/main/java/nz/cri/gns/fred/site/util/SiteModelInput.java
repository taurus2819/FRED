/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * This class is facade on the SiteModel used for convenience of client for POST and PUT
 * @author scaddenp
 */
public class SiteModelInput {
    private final SiteModel siteModel;
    private int epsg;
    private String gridref;
    private Double easting;
    private Double northing;
    private String latitude;
    private String longitude;
    private String format;
    private String auditMsg;

    public SiteModelInput() {
        this.siteModel = new SiteModel();
    }

    public SiteModelInput(String siteName, Integer methodId, Double accuracy, String Directions, 
               Double height, Integer heightMethodId, Double heightAccuracy, String countyCode, String comment, Integer ownerId,
               int epsg, String gridref, Double easting, Double northing, String latitude, String longitude, String format, String auditMsg) {
        this.siteModel = new SiteModel();
        this.siteModel.setSiteName(siteName);
        this.siteModel.setMethodId(methodId);
        this.siteModel.setAccuracy(accuracy);
        this.siteModel.setDirections(Directions);
        this.siteModel.setHeight(height);
        this.siteModel.setHeightMethodId(heightMethodId);
        this.siteModel.setHeightAccuracy(heightAccuracy);
        this.siteModel.setCountryCode(countyCode);
        this.siteModel.setComment(comment);
        this.siteModel.setOwnerId(ownerId);
        this.epsg = epsg;
        this.gridref = gridref;
        this.easting = easting;
        this.northing = northing;
        this.latitude = latitude;
        this.longitude = longitude;
        this.format = format;
        this.auditMsg = auditMsg;
    }
    
    
    public String getSiteName() {
        return siteModel.getSiteName();
    }

    public void setSiteName(String siteName) {
        this.siteModel.setSiteName(siteName);
    }


    public Integer getMethodId() {
        return siteModel.getMethodId();
    }

    public void setMethodId(Integer methodId) {
        this.siteModel.setMethodId(methodId);
    }

    public Double getAccuracy() {
        return siteModel.getAccuracy();
    }

    public void setAccuracy(Double accuracy) {
        this.siteModel.setAccuracy(accuracy);
    }

    public String getDirections() {
        return siteModel.getDirections();
    }

    public void setDirections(String directions) {
        this.siteModel.setDirections(directions);
    }

    
    public Double getHeight() {
        return siteModel.getHeight();
    }

    public void setHeight(Double height) {
        this.siteModel.setHeight(height);
    }

    public Integer getHeightMethodId() {
        return siteModel.getHeightMethodId();
    }

    public void setHeightMethodId(Integer heightMethodId) {
        this.siteModel.setHeightMethodId(heightMethodId);
    }

    public Double getHeightAccuracy() {
        return siteModel.getHeightAccuracy();
    }

    public void setHeightAccuracy(Double heightAccuracy) {
        this.siteModel.setHeightAccuracy(heightAccuracy);
    }

    public String getCountryCode() {
        return siteModel.getCountryCode();
    }

    public void setCountryCode(String countryCode) {
        this.siteModel.setCountryCode(countryCode);
    }

    public String getComment() {
        return siteModel.getComment();
    }

    public void setComment(String comment) {
        this.siteModel.setComment(comment);
    }

    public Integer getOwnerId(){
        return this.siteModel.getOwnerId();
    }
    
    public void setOwnerId(Integer OwnerId){
        this.siteModel.setOwnerId(OwnerId);
    }
    
    public int getEpsg() {
        return epsg;
    }

    public void setEpsg(int epsg) {
        this.epsg = epsg;
    }

    public String getGridref() {
        return gridref;
    }

    public void setGridref(String gridref) {
        this.gridref = gridref;
    }

    public Double getEasting() {
        return easting;
    }

    public void setEasting(Double easting) {
        this.easting = easting;
    }

    public Double getNorthing() {
        return northing;
    }

    public void setNorthing(Double northing) {
        this.northing = northing;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAuditMsg() {
        return auditMsg;
    }

    public void setAuditMsg(String auditMsg) {
        this.auditMsg = auditMsg;
    }
    

}
