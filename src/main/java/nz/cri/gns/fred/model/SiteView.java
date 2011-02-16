package nz.cri.gns.fred.model;

import java.util.Set;

public interface SiteView {

	public void setSiteId(Integer siteId);
	public Integer getSiteId();
	public void setSiteName(String siteName);
	public String getSiteName();
	public void setLatitude(Double latitude);
	public Double getLatitude();
	public void setLongitude(Double longitude);
	public Double getLongitude();
	public void setCountryCode(String countryCode);
	public String getCountryCode();
	public void setCountryName(String countryName);
	public String getCountryName();
	public void setNzmgSheet(String nzmgSheet);
	public String getNzmgSheet();
	public void setNzmgEast(Double nzmgEast);
	public Double getNzmgEast();
	public void setNzmgNorth(Double nzmgNorth);
	public Double getNzmgNorth();
	public void setQmapSheet(String qmapSheet);
	public String getQmapSheet();
	public void setNzms262Sheet(String nzms262Sheet);
	public String getNzms262Sheet();
	public void setIsland(String island);
	public String getIsland();
	public void setMethodId(Integer methodId);
	public Integer getMethodId();
	public void setMethod(String method);
	public String getMethod();
	public void setAccuracy(Double accuracy);
	public Double getAccuracy();
	public void setDirections(String directions);
	public String getDirections();
	public void setOrigSystemId(Integer origSystemId);
	public Integer getOrigSystemId();
	public void setCoordSystem(String coordSystem);
	public String getCoordSystem();
	public void setOrigCoord(String origCoord);
	public String getOrigCoord();
	public void setHeight(Double height);
	public Double getHeight();
	public void setHeightMethodId(Integer heightMethodId);
	public Integer getHeightMethodId();
	public void setHeightMethod(String heightMethod);
	public String getHeightMethod();
	public void setHeightAccuracy(Double heightAccuracy);
	public Double getHeightAccuracy();
	public void setOwnerId(Integer ownerId);
	public Integer getOwnerId();
	public void setOwner(String owner);
	public String getOwner();
	public void setFeatures(Set<Feature> features);
	public Set<Feature> getFeatures();

}
