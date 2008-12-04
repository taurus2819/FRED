package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;

public class Stage implements Serializable, nz.cri.gns.fred.model.Stage {

    private static final long serialVersionUID = 20050818L;

    private Integer stageId;
    private String stageLowerMod;
    private String stageUpperMod;
    private Age lowerAge;
    private Age upperAge;
    private Set<Sample> samplesByKnownStageId;
    private Set<Sample> samplesByInferredStageId;
    private Set<Paleontology> paleontologies;
    private Set<Adoption> adoptions;

    public Integer getStageId() {
        return this.stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }

    public String getStageLowerMod() {
        return this.stageLowerMod;
    }

    public void setStageLowerMod(String stageLowerMod) {
        this.stageLowerMod = stageLowerMod;
    }

    public String getStageUpperMod() {
        return this.stageUpperMod;
    }

    public void setStageUpperMod(String stageUpperMod) {
        this.stageUpperMod = stageUpperMod;
    }
    
	public Age getLowerAge() {
		return lowerAge;
	}

    public void setLowerAge(Age lowerAge) {
		this.lowerAge = lowerAge;
	}

	public Age getUpperAge() {
		return upperAge;
	}
	
	public void setUpperAge(Age upperAge) {
		this.upperAge = upperAge;
	}

	public Set<Sample> getSamplesByKnownStageId() {
        return this.samplesByKnownStageId;
    }

    public void setSamplesByKnownStageId(Set<Sample> samplesByKnownStageId) {
        this.samplesByKnownStageId = samplesByKnownStageId;
    }

    public Set<Sample> getSamplesByInferredStageId() {
        return this.samplesByInferredStageId;
    }

    public void setSamplesByInferredStageId(Set<Sample> samplesByInferredStageId) {
        this.samplesByInferredStageId = samplesByInferredStageId;
    }

    public Set<Paleontology> getPaleontologies() {
        return this.paleontologies;
    }

    public void setPaleontologies(Set<Paleontology> paleontologies) {
        this.paleontologies = paleontologies;
    }

    public Set<Adoption> getAdoptions() {
        return this.adoptions;
    }

    public void setAdoptions(Set<Adoption> adoptions) {
        this.adoptions = adoptions;
    }

	public boolean equals(Object o) {
		return o instanceof Stage && ((Stage)o).stageId.equals(stageId);
	}
	
	public int hashCode() {
		return 487 * stageId;
	}
}