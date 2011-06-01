package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface RegistrationArea extends Comparable<RegistrationArea>, NameableAndIdentifiable {
	public Integer getRegAreaId();
    public void setRegAreaId(Integer regAreaId);
    public String getName();
    public void setName(String name);
    public String getCode();
    public void setCode(String code);
    public Set<Feature> getFeatures();
    public void setFeatures(Set<Feature> features);
}
