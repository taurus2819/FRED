package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Carbonate extends Comparable<Carbonate>, NameableAndIdentifiable {
	public Integer getCarbonateId();
	public void setCarbonateId(Integer carbonateId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
}