package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface RockColour extends Comparable<RockColour>, NameableAndIdentifiable {
	public Integer getColourId();
	public void setColourId(Integer colourId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
}