package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Carbonate extends Comparable<Carbonate>, NameableAndIdentifiable {
	public Integer getCarbonateId();
	public void setCarbonateId(Integer carbonateId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}