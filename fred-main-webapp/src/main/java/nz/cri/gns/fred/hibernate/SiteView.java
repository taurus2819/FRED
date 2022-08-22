package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Feature;

public class SiteView implements Serializable, nz.cri.gns.fred.model.SiteView {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer siteId;

    /** nullable persistent field */
    private String siteName;

    /** nullable persistent field */
    private Double latitude;

    /** nullable persistent field */
    private Double longitude;
    
    /** nullable persistent field */
    private String countryCode;
    
    /** nullable persistent field */
    private String countryName;
    
    /** nullable persistent field */
    private String nzmgSheet;

    /** persistent field */
    private Double nzmgEast;

    /** nullable persistent field */
    private Double nzmgNorth;

    /** nullable persistent field */
    private String qmapSheet;

    /** nullable persistent field */
    private String nzms262Sheet;
    
    /** nullable persistent field */
    private String nzms260Sheet;
    
    /** nullable persistent field */
    private String topo50Sheet;
    
    /** nullable persistent field */
    private String island;

    /** nullable persistent field */
    private Integer methodId;

    /** nullable persistent field */
    private String method;

    /** nullable persistent field */
    private Double accuracy;

    /** nullable persistent field */
    private String directions;

    /** nullable persistent field */
    private Integer origSystemId;
    
    /** nullable persistent field */
    private String coordSystem;

    /** nullable persistent field */
    private String origCoord;

    /** nullable persistent field */
    private Double height;

    /** nullable persistent field */
    private Integer heightMethodId;

    /** nullable persistent field */
    private String heightMethod;
    
    /** nullable persistent field */
    private Double heightAccuracy;

    /** nullable persistent field */
    private Integer ownerId;

    /** nullable persistent field */
    private String owner;
    
    /** persistent field */
    private Set<Feature> features;

    /** full constructor */
    public SiteView(Integer siteId, String siteName, Double latitude, Double longitude, String countryCode, String countryName, String nzmgSheet, Double nzmgEast, Double nzmgNorth, String qmapSheet, String nzms262Sheet, String nzms260Sheet, String topo50Sheet, String island, Integer methodId, String method, Double accuracy, String directions, Integer origSystemId, String coordSystem, String origCoord, Double height, Integer heightMethodId, String heightMethod, Double heightAccuracy, Integer ownerId, String owner) {
    	this.siteId = siteId;
    	this.siteName = siteName;
    	this.latitude = latitude;
    	this.longitude = longitude;
    	this.countryCode = countryCode;
    	this.countryName = countryName;
    	this.nzmgSheet = nzmgSheet;
    	this.nzmgEast = nzmgEast;
    	this.nzmgNorth = nzmgNorth;
    	this.qmapSheet = qmapSheet;
    	this.nzms262Sheet = nzms262Sheet;
    	this.nzms260Sheet = nzms260Sheet;
    	this.topo50Sheet = topo50Sheet;
    	this.island = island;
    	this.methodId = methodId;
    	this.method = method;
    	this.accuracy = accuracy;
    	this.directions = directions;
    	this.origSystemId = origSystemId;
    	this.coordSystem = coordSystem;
    	this.origCoord = origCoord;
    	this.height = height;
    	this.heightMethodId = heightMethodId; 
    	this.heightMethod = heightMethod;
    	this.heightAccuracy = heightAccuracy;
    	this.ownerId = ownerId;
    	this.owner = owner;
    }

    /** default constructor */
    public SiteView() {
    }

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setNzmgSheet(String nzmgSheet) {
		this.nzmgSheet = nzmgSheet;
	}

	public String getNzmgSheet() {
		return nzmgSheet;
	}

	public void setNzmgEast(Double nzmgEast) {
		this.nzmgEast = nzmgEast;
	}

	public Double getNzmgEast() {
		return nzmgEast;
	}

	public void setNzmgNorth(Double nzmgNorth) {
		this.nzmgNorth = nzmgNorth;
	}

	public Double getNzmgNorth() {
		return nzmgNorth;
	}

	public void setQmapSheet(String qmapSheet) {
		this.qmapSheet = qmapSheet;
	}

	public String getQmapSheet() {
		return qmapSheet;
	}

	public void setNzms262Sheet(String nzms262Sheet) {
		this.nzms262Sheet = nzms262Sheet;
	}

	public String getNzms262Sheet() {
		return nzms262Sheet;
	}

        public String getNzms260Sheet() {
            return nzms260Sheet;
        }

        public void setNzms260Sheet(String nzms260Sheet) {
            this.nzms260Sheet = nzms260Sheet;
        }

        public String getTopo50Sheet() {
            return topo50Sheet;
        }

        public void setTopo50Sheet(String topo50Sheet) {
            this.topo50Sheet = topo50Sheet;
        }        
        
	public void setIsland(String island) {
		this.island = island;
	}

	public String getIsland() {
		return island;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public String getDirections() {
		return directions;
	}

	public void setOrigSystemId(Integer origSystemId) {
		this.origSystemId = origSystemId;
	}

	public Integer getOrigSystemId() {
		return origSystemId;
	}

	public void setCoordSystem(String coordSystem) {
		this.coordSystem = coordSystem;
	}

	public String getCoordSystem() {
		return coordSystem;
	}

	public void setOrigCoord(String origCoord) {
		this.origCoord = origCoord;
	}

	public String getOrigCoord() {
		return origCoord;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeightMethodId(Integer heightMethodId) {
		this.heightMethodId = heightMethodId;
	}

	public Integer getHeightMethodId() {
		return heightMethodId;
	}

	public void setHeightMethod(String heightMethod) {
		this.heightMethod = heightMethod;
	}

	public String getHeightMethod() {
		return heightMethod;
	}

	public void setHeightAccuracy(Double horizAccuracy) {
		this.heightAccuracy = horizAccuracy;
	}

	public Double getHeightAccuracy() {
		return heightAccuracy;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}

	public Set<Feature> getFeatures() {
		return features;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SiteView && ((SiteView)o).siteId.equals(siteId);
	}
	
	@Override
	public int hashCode() {
		return 037 * siteId;
	}

}
