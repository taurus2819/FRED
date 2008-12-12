package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleStageView;

public class Stage implements Serializable, nz.cri.gns.fred.model.Stage {

    private static final long serialVersionUID = 20050818L;

    private Integer stageId;
    private String stageLowerMod;
    private String stageUpperMod;
    private Double baseAge;
    private Double topAge;
    private Age lowerAge;
    private Age upperAge;
    private Set<Sample> samplesByKnownStageId;
    private Set<Sample> samplesByInferredStageId;
    private Set<Paleontology> paleontologies;
    private Set<Adoption> adoptions;
    private Set<SampleStageView> sampleStageViews;

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
    
	public void setBaseAge(Double baseAge) {
		this.baseAge = baseAge;
	}

	public Double getBaseAge() {
		return baseAge;
	}

	public void setTopAge(Double topAge) {
		this.topAge = topAge;
	}

	public Double getTopAge() {
		return topAge;
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

	public void setSampleStageViews(Set<SampleStageView> sampleStageViews) {
		this.sampleStageViews = sampleStageViews;
	}

	public Set<SampleStageView> getSampleStageViews() {
		return sampleStageViews;
	}

	public boolean equals(Object o) {
		return o instanceof Stage && ((Stage)o).stageId.equals(stageId);
	}
	
	public int hashCode() {
		return 487 * stageId;
	}

	public int compareTo(nz.cri.gns.fred.model.Stage arg0) {
		if (baseAge.equals(arg0.getBaseAge()))
			return topAge.compareTo(arg0.getTopAge());
		return baseAge.compareTo(arg0.getBaseAge());
	}
}