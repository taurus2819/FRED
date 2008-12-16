package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Age extends Comparable<Age>, NameableAndIdentifiable {
    public void setAgeId(Integer siteId);
	public Integer getAgeId();
	public void setName(String name);
	public String getName();
	public void setCode(String code);
	public String getCode();
	public void setPeriod(String period);
	public String getPeriod();
	public void setBaseAge(Double baseAge);
	public Double getBaseAge();
	public void setTopAge(Double topAge);
	public Double getTopAge();
	public void setComments(String comments);
	public String getComments();
	public void setObsoleteFlag(Boolean obsoleteFlag);
	public Boolean getObsoleteFlag();
	public void setDuplicateFlag(Boolean duplicateFlag);
	public Boolean getDuplicateFlag();
	public void setStagesByAgeLowerId(Set<Stage> stagesByAgeLowerId);
	public Set<Stage> getStagesByAgeLowerId();
	public void setStagesByAgeUpperId(Set<Stage> stagesByAgeUpperId);
	public Set<Stage> getStagesByAgeUpperId();
}
