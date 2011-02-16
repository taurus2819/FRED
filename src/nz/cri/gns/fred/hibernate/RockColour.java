package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class RockColour implements Serializable, nz.cri.gns.fred.model.RockColour {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer colourId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Sample> samplesBySecondaryColourId;

    /** persistent field */
    private Set<Sample> samplesByPrimaryColourId;

    /** full constructor */
    public RockColour(Integer colourId, String name, String code, Set<Sample> samplesBySecondaryColourId, Set<Sample> samplesByPrimaryColourId) {
        this.colourId = colourId;
        this.name = name;
        this.code = code;
        this.samplesBySecondaryColourId = samplesBySecondaryColourId;
        this.samplesByPrimaryColourId = samplesByPrimaryColourId;
    }

    /** default constructor */
    public RockColour() {
    }

    public Integer getColourId() {
        return this.colourId;
    }

    public void setColourId(Integer colourId) {
        this.colourId = colourId;
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

    public Set<Sample> getSamplesBySecondaryColourId() {
        return this.samplesBySecondaryColourId;
    }

    public void setSamplesBySecondaryColourId(Set<Sample> samplesBySecondaryColourId) {
        this.samplesBySecondaryColourId = samplesBySecondaryColourId;
    }

    public Set<Sample> getSamplesByPrimaryColourId() {
        return this.samplesByPrimaryColourId;
    }

    public void setSamplesByPrimaryColourId(Set<Sample> samplesByPrimaryColourId) {
        this.samplesByPrimaryColourId = samplesByPrimaryColourId;
    }

    @Override
	public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.RockColour arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.colourId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RockColour && ((RockColour)o).colourId.equals(colourId);
	}
	
	@Override
	public int hashCode() {
		return 243 * colourId;
	}
}
