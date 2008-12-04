package nz.cri.gns.fred.model;

import java.util.Set;

public interface Stage {
	public Integer getStageId();
	public void setStageId(Integer stageId);
	public String getStageLowerMod();
	public void setStageLowerMod(String stageLowerMod);
	public String getStageUpperMod();
	public void setStageUpperMod(String stageUpperMod);
	public Age getLowerAge();
    public void setLowerAge(Age lowerAge);
	public Age getUpperAge();
	public void setUpperAge(Age upperAge);
	public Set<Sample> getSamplesByKnownStageId();
	public void setSamplesByKnownStageId(Set<Sample> samplesByKnownStageId);
	public Set<Sample> getSamplesByInferredStageId();
	public void setSamplesByInferredStageId(Set<Sample> samplesByInferredStageId);
	public Set<Paleontology> getPaleontologies();
	public void setPaleontologies(Set<Paleontology> paleontologies);
	public Set<Adoption> getAdoptions();
	public void setAdoptions(Set<Adoption> adoptions);
}