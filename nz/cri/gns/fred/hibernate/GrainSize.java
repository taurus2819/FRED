package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class GrainSize implements Serializable, nz.cri.gns.fred.model.GrainSize {

    private static final long serialVersionUID = 20050818L;
    
    /** identifier field */
    private Integer grainSizeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Sample> samplesByPrimaryGrainsizeId;

    /** persistent field */
    private Set<Sample> samplesBySecondaryGrainsizeId;

    /** full constructor */
    public GrainSize(Integer grainSizeId, String name, String code, Set<Sample> samplesByPrimaryGrainsizeId, Set<Sample> samplesBySecondaryGrainsizeId) {
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

    public Set<Sample> getSamplesByPrimaryGrainsizeId() {
        return this.samplesByPrimaryGrainsizeId;
    }

    public void setSamplesByPrimaryGrainsizeId(Set<Sample> samplesByPrimaryGrainsizeId) {
        this.samplesByPrimaryGrainsizeId = samplesByPrimaryGrainsizeId;
    }

    public Set<Sample> getSamplesBySecondaryGrainsizeId() {
        return this.samplesBySecondaryGrainsizeId;
    }

    public void setSamplesBySecondaryGrainsizeId(Set<Sample> samplesBySecondaryGrainsizeId) {
        this.samplesBySecondaryGrainsizeId = samplesBySecondaryGrainsizeId;
    }

    public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.GrainSize arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.grainSizeId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}


}
