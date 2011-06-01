package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface SedimentaryFeatureType extends Comparable<SedimentaryFeatureType>, NameableAndIdentifiable {
	public Integer getSedfeatureTypeId();
	public void setSedfeatureTypeId(Integer sedfeatureTypeId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<SedimentaryFeature> getSedimentaryFeatures();
	public void setSedimentaryFeatures(Set<SedimentaryFeature> sedimentaryFeatures);
}