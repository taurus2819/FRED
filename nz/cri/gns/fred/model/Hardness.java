package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Hardness extends Comparable<Hardness>, NameableAndIdentifiable {
	public Integer getHardnessId();
	public void setHardnessId(Integer hardnessId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}