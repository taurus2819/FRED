package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface RockColour extends Comparable<RockColour>, NameableAndIdentifiable {
	public Integer getColourId();
	public void setColourId(Integer colourId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamplesBySecondaryColourId();
	public void setSamplesBySecondaryColourId(Set<Sample> samplesBySecondaryColourId);
	public Set<Sample> getSamplesByPrimaryColourId();
	public void setSamplesByPrimaryColourId(Set<Sample> samplesByPrimaryColourId);
}