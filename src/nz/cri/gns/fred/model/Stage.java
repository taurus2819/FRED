package nz.cri.gns.fred.model;

import java.util.Set;

public interface Stage extends Comparable<Stage> {
	public Integer getStageId();
	public void setStageId(Integer stageId);
	public String getStageLowerMod();
	public void setStageLowerMod(String stageLowerMod);
	public String getStageUpperMod();
	public void setStageUpperMod(String stageUpperMod);
	public void setBaseAge(Double baseAge);
	public Double getBaseAge();
	public void setTopAge(Double topAge);
	public Double getTopAge();
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
	public void setSampleStageViews(Set<SampleStageView> sampleStageViews);
	public Set<SampleStageView> getSampleStageViews();
	public void setRecordStageViews(Set<RecordStageView> recordStageViews);
	public Set<RecordStageView> getRecordStageViews();
}