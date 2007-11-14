package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface AgeView extends Comparable<AgeView>, NameableAndIdentifiable {
	public void setAgeId(Integer ageId);
	public Integer getAgeId();
	public void setAgeName(String ageName);
	public String getAgeName();
	public void setAgeAbbrev(String ageAbbrev);
	public String getAgeAbbrev();
	public void setAgeStop(Double ageStop);
	public Double getAgeStop();
	public void setAgeStart(Double ageStart);
	public Double getAgeStart();
	public void setStagesByStageLowerId(Set<Stage> stagesByStageLowerId);
	public Set<Stage> getStagesByStageLowerId();
	public void setStagesByStageUpperId(Set<Stage> stagesByStageUpperId);
	public Set<Stage> getStagesByStageUpperId();
}
