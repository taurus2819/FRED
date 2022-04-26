package nz.cri.gns.fred.site.util;

/**
 *
 * @author sitikond
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.core.style.ToStringCreator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteModel {
    
    
    private Integer siteId;  //51955
    private String siteName;  //GNS Physical Location Site, Dunedin
    private double lat;		//-45.864369921
    private double lon;		//170.513135754
    private Integer methodId;	//3
    private Double accuracy;		//10
    private String directions;	//764 Cumberland Street, Dunedin
    private Integer origSystemId; 	//38
    private JsonNode origCoord;	
    private Double height;		//44
    private Integer heightMethodId;		//3
    private Double heightAccuracy;	//10
    private String countryCode;		//NZ
    private Integer flag;				//null
    private String comment;			//"blah blah"
//    private Geometry shape;
    private Integer ownerId;
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    public SiteModel() {

    }

    public SiteModel(String siteName, double lat, double lon, Integer methodId, Double accuracy,
            String directions, Integer origSystemId, JsonNode origCoord, Double height, Integer heightMethodId,
            Double heightAccuracy, String countryCode, Integer flag, String comment, Integer ownerId, String auditMsg ){  //   /*, String shape*/, String auditlogInfoMsg) {
        super();
        this.siteName = siteName;
        this.lat = lat;
        this.lon = lon;
        this.methodId = methodId;
        this.accuracy = accuracy;
        this.directions = directions;
        this.origSystemId = origSystemId;
        this.origCoord = origCoord;
        this.height = height;
        this.heightMethodId = heightMethodId;
        this.heightAccuracy = heightAccuracy;
        this.countryCode = countryCode;
        this.flag = flag;
        this.comment = comment;
//        this.shape = shape;
        this.ownerId = ownerId;
    }

    public Integer getSiteId() {
        return this.siteId;
    }    

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Integer getOrigSystemId() {
        return origSystemId;
    }

    public void setOrigSystemId(Integer origSystemId) {
        this.origSystemId = origSystemId;
    }

    public JsonNode getOrigCoord() {
        return origCoord;
    }

    public void setOrigCoord(JsonNode origCoord) {
        this.origCoord = origCoord;
    }
    
    public Double getHeight() {
          return height;          
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getHeightMethodId() {
        return heightMethodId;
    }

    public void setHeightMethodId(Integer heightMethodId) {
        this.heightMethodId = heightMethodId;
    }

    public Double getHeightAccuracy() {
        return heightAccuracy;
    }

    public void setHeightAccuracy(Double heightAccuracy) {
        this.heightAccuracy = heightAccuracy;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public Geometry getShape() {        
//        return shape;
//    }
//
//    public void setShape(Geometry shape) {
//        shape.setSRID(4326);
//        this.shape = shape;
//    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }   
    
    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }
    
    public boolean addAuditLog(AuditLog auditLogInfo){
//        auditLogInfo.setSiteModel(this);
        return getAuditLogs().add(auditLogInfo);
    }
    
    public void removeAuditLogs(){
        
    }
    
    
    @Override
    public String toString(){
//        JsonNode jsonnode = this.getOrigCoord();
//        System.out.println("JsonNode length= " + jsonnode.size());
//         System.out.println("JsonNode length= " + jsonnode.getNodeType());
//         System.out.println("JsonNode length= " + jsonnode.get(1));
        return new ToStringCreator(this)
                .append("id", this.getSiteId())
                .append("site_name", this.getSiteName())
                .append("lat", this.getLat())
                .append("lon", this.getLon())
                .append("OrigSysId", this.getOrigSystemId())
                .append("OrigCoord", this.getOrigCoord())
//                .append("Shape", this.getShape())
                .toString();
    }
}
