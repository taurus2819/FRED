package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface Person extends PersonRelationship {
	
	public abstract Integer getPersonId();

	public abstract void setPersonId(Integer personId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getStCode();

	public abstract void setStCode(Integer stCode);

	public abstract Set getAdoptions();

	public abstract void setAdoptions(Set adoptions);

	public abstract Set getIdentifiedPaleontologies();

	public abstract void setIdentifiedPaleontologies(
			Set identifiedPaleontologies);

	public abstract Set getFeatures();

	public abstract void setFeatures(Set features);

	public abstract Set getCollectedSamples();

	public abstract void setCollectedSamples(Set collectedSamples);
}
