package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface SedimentaryFeatureType {
	public abstract Integer getSedfeatureTypeId();

	public abstract void setSedfeatureTypeId(Integer sedfeatureTypeId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Set getSedimentaryFeatures();

	public abstract void setSedimentaryFeatures(Set sedimentaryFeatures);
}