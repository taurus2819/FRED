package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Bedding  extends Comparable<Bedding>, NameableAndIdentifiable {
	public Integer getBeddingId();
	public void setBeddingId(Integer beddingId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamplesByPrimaryBeddingId();
	public void setSamplesByPrimaryBeddingId(Set<Sample> samplesByPrimaryBeddingId);
	public Set<Sample> getSamplesBySecondaryBeddingId();
	public void setSamplesBySecondaryBeddingId(Set<Sample> samplesBySecondaryBeddingId);
}