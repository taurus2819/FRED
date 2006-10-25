package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface GrainSize extends Comparable<GrainSize>, NameableAndIdentifiable {
	public Integer getGrainSizeId();
	public void setGrainSizeId(Integer grainSizeId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamplesByPrimaryGrainsizeId();
	public void setSamplesByPrimaryGrainsizeId(Set<Sample> samplesByPrimaryGrainsizeId);
	public Set<Sample> getSamplesBySecondaryGrainsizeId();
	public void setSamplesBySecondaryGrainsizeId(Set<Sample> samplesBySecondaryGrainsizeId);
}