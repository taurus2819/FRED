package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Weathering extends Comparable<Weathering>, NameableAndIdentifiable {
	public Integer getWeatheringId();
	public void setWeatheringId(Integer weatheringId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}