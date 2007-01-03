package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Person extends PersonRelationship, Comparable<Person>, NameableAndIdentifiable {
	public Integer getPersonId();
	public void setPersonId(Integer personId);
	public String getName();
	public void setName(String name);
	public Integer getStCode();
	public void setStCode(Integer stCode);
	public Set<Adoption> getAdoptions();
	public void setAdoptions(Set<Adoption> adoptions);
	public Set<Paleontology> getIdentifiedPaleontologies();
	public void setIdentifiedPaleontologies(Set<Paleontology> identifiedPaleontologies);
	public Set<Feature> getFeatures();
	public void setFeatures(Set<Feature> features);
	public Set<Sample> getCollectedSamples();
	public void setCollectedSamples(Set<Sample> collectedSamples);
	public Set<SentTo> getSentTos();
	public void setSentTos(Set<SentTo> sentTos);
}
