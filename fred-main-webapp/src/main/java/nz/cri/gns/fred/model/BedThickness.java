package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface BedThickness extends Comparable<BedThickness>, NameableAndIdentifiable {
	public Integer getThicknessId();
	public void setThicknessId(Integer thicknessId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
}