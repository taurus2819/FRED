package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface ColourModifier extends Comparable<ColourModifier>, NameableAndIdentifiable {
	public Integer getModifierId();
	public void setModifierId(Integer modifierId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}