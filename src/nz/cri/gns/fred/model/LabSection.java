package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface LabSection extends Comparable<LabSection>, NameableAndIdentifiable {
	public Integer getLabSectionId();
	public void setLabSectionId(Integer labSectionId);
	public Lab getLab();
	public void setLab(Lab lab);
	public String getName();
	public void setName(String name);
	public String getCode();
	public void setCode(String code);
	public String getClosed();
	public void setClosed(String closed);
	public Set<Paleontology> getPaleontologies();
	public void setPaleontologies(Set<Paleontology> paleontologies);
}