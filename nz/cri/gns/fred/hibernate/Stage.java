package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class Stage implements Serializable, nz.cri.gns.fred.model.Stage {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer stageId;

    /** persistent field */
    private Integer stageLowerId;

    /** nullable persistent field */
    private String stageLowerMod;

    /** nullable persistent field */
    private Integer stageUpperId;

    /** nullable persistent field */
    private String stageUpperMod;

    /** nullable persistent field */
    private String stageMod;

    /** persistent field */
    private Set samplesByKnownStageId;

    /** persistent field */
    private Set samplesByInferredStageId;

    /** persistent field */
    private Set paleontologies;

    /** persistent field */
    private Set adoptions;

    /** full constructor */
    public Stage(Integer stageLowerId, String stageLowerMod, Integer stageUpperId, String stageUpperMod, String stageMod, Set samplesByKnownStageId, Set samplesByInferredStageId, Set paleontologies, Set adoptions) {
        this.stageLowerId = stageLowerId;
        this.stageLowerMod = stageLowerMod;
        this.stageUpperId = stageUpperId;
        this.stageUpperMod = stageUpperMod;
        this.stageMod = stageMod;
        this.samplesByKnownStageId = samplesByKnownStageId;
        this.samplesByInferredStageId = samplesByInferredStageId;
        this.paleontologies = paleontologies;
        this.adoptions = adoptions;
    }

    /** default constructor */
    public Stage() {
    }

    /** minimal constructor */
    public Stage(Integer stageLowerId, Set samplesByKnownStageId, Set samplesByInferredStageId, Set paleontologies, Set adoptions) {
        this.stageLowerId = stageLowerId;
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

    public Integer getStageLowerId() {
        return this.stageLowerId;
    }

    public void setStageLowerId(Integer stageLowerId) {
        this.stageLowerId = stageLowerId;
    }

    public String getStageLowerMod() {
        return this.stageLowerMod;
    }

    public void setStageLowerMod(String stageLowerMod) {
        this.stageLowerMod = stageLowerMod;
    }

    public Integer getStageUpperId() {
        return this.stageUpperId;
    }

    public void setStageUpperId(Integer stageUpperId) {
        this.stageUpperId = stageUpperId;
    }

    public String getStageUpperMod() {
        return this.stageUpperMod;
    }

    public void setStageUpperMod(String stageUpperMod) {
        this.stageUpperMod = stageUpperMod;
    }

    public String getStageMod() {
        return this.stageMod;
    }

    public void setStageMod(String stageMod) {
        this.stageMod = stageMod;
    }

    public Set getSamplesByKnownStageId() {
        return this.samplesByKnownStageId;
    }

    public void setSamplesByKnownStageId(Set samplesByKnownStageId) {
        this.samplesByKnownStageId = samplesByKnownStageId;
    }

    public Set getSamplesByInferredStageId() {
        return this.samplesByInferredStageId;
    }

    public void setSamplesByInferredStageId(Set samplesByInferredStageId) {
        this.samplesByInferredStageId = samplesByInferredStageId;
    }

    public Set getPaleontologies() {
        return this.paleontologies;
    }

    public void setPaleontologies(Set paleontologies) {
        this.paleontologies = paleontologies;
    }

    public Set getAdoptions() {
        return this.adoptions;
    }

    public void setAdoptions(Set adoptions) {
        this.adoptions = adoptions;
    }


}
