package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface RegistrationArea {
	
	public Integer getRegAreaId();
    public void setRegAreaId(Integer regAreaId);
    public String getName();
    public void setName(String name);
    public String getCode();
    public void setCode(String code);
    public Set getFeatures();
    public void setFeatures(Set features);
}
