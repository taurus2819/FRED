package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Hardness extends Comparable<Hardness>, NameableAndIdentifiable {
	public Integer getHardnessId();
	public void setHardnessId(Integer hardnessId);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
}