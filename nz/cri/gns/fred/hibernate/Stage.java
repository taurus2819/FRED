package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.AgeView;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class Stage implements Serializable, nz.cri.gns.fred.model.Stage {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer stageId;

    /** nullable persistent field */
    private String stageLowerMod;

    /** nullable persistent field */
    private String stageUpperMod;

    /** persistent field */
    private AgeView lowerAgeView;
    
    /** persistent field */
    private AgeView upperAgeView;
    
    /** persistent field */
    private Set<Sample> samplesByKnownStageId;

    /** persistent field */
    private Set<Sample> samplesByInferredStageId;

    /** persistent field */
    private Set<Paleontology> paleontologies;

    /** persistent field */
    private Set<Adoption> adoptions;

    /** full constructor */
    public Stage(String stageLowerMod, String stageUpperMod, AgeView lowerAgeView, AgeView upperAgeView, Set<Sample> samplesByKnownStageId, Set<Sample> samplesByInferredStageId, Set<Paleontology> paleontologies, Set<Adoption> adoptions) {
        this.stageLowerMod = stageLowerMod;
        this.stageUpperMod = stageUpperMod;
        this.setLowerAgeView(lowerAgeView);
        this.setUpperAgeView(upperAgeView);
        this.samplesByKnownStageId = samplesByKnownStageId;
        this.samplesByInferredStageId = samplesByInferredStageId;
        this.paleontologies = paleontologies;
        this.adoptions = adoptions;
    }

    /** default constructor */
    public Stage() {
    }

    /** minimal constructor */
    public Stage(Set<Sample> samplesByKnownStageId, Set<Sample> samplesByInferredStageId, Set<Paleontology> paleontologies, Set<Adoption> adoptions) {
        this.samplesByKnownStageId = samplesByKnownStageId;
        this.samplesByInferredStageId = samplesByInferredStageId;
        this.paleontologies = paleontologies;
        this.adoptions = adoptions;
    }

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
    
	public AgeView getLowerAgeView() {
		return lowerAgeView;
	}

    public void setLowerAgeView(AgeView lowerAgeView) {
		this.lowerAgeView = lowerAgeView;
	}

	public AgeView getUpperAgeView() {
		return upperAgeView;
	}
	
	public void setUpperAgeView(AgeView upperAgeView) {
		this.upperAgeView = upperAgeView;
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

}
