package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Weathering extends Comparable<Weathering>, NameableAndIdentifiable {
	public Integer getWeatheringId();
	public void setWeatheringId(Integer weatheringId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
}