package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface DrillType extends Comparable<DrillType>, NameableAndIdentifiable {
	public Integer getDrillTypeId();
	public void setDrillTypeId(Integer drillTypeId);
	public String getName();
	public void setName(String name);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}