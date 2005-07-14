package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class GrainSize implements Serializable {

    /** identifier field */
    private Integer grainSizeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samplesByPrimaryGrainsizeId;

    /** persistent field */
    private Set samplesBySecondaryGrainsizeId;

    /** full constructor */
    public GrainSize(Integer grainSizeId, String name, String code, Set samplesByPrimaryGrainsizeId, Set samplesBySecondaryGrainsizeId) {
        this.grainSizeId = grainSizeId;
        this.name = name;
        this.code = code;
        this.samplesByPrimaryGrainsizeId = samplesByPrimaryGrainsizeId;
        this.samplesBySecondaryGrainsizeId = samplesBySecondaryGrainsizeId;
    }

    /** default constructor */
    public GrainSize() {
    }

    public Integer getGrainSizeId() {
        return this.grainSizeId;
    }

    public void setGrainSizeId(Integer grainSizeId) {
        this.grainSizeId = grainSizeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set getSamplesByPrimaryGrainsizeId() {
        return this.samplesByPrimaryGrainsizeId;
    }

    public void setSamplesByPrimaryGrainsizeId(Set samplesByPrimaryGrainsizeId) {
        this.samplesByPrimaryGrainsizeId = samplesByPrimaryGrainsizeId;
    }

    public Set getSamplesBySecondaryGrainsizeId() {
        return this.samplesBySecondaryGrainsizeId;
    }

    public void setSamplesBySecondaryGrainsizeId(Set samplesBySecondaryGrainsizeId) {
        this.samplesBySecondaryGrainsizeId = samplesBySecondaryGrainsizeId;
    }

    public String toString() {
        return name;
    }


}
